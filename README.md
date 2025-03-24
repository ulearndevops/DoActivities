 Got it! You will provide the new <Settings> content, and the script should find all <Settings> tags inside the XML file and replace their inner content while keeping everything else unchanged.


---

PowerShell Script:

# Define the XML file path
$xmlFile = "C:\path\to\config.xml"

# Define the new <Settings> content (Replace this as per your input)
$newSettingsContent = @"
<Settings>
    <Option1>NewValue1</Option1>
    <Option2>NewValue2</Option2>
</Settings>
"@

# Load the XML
[xml]$xml = Get-Content $xmlFile

# Convert the new <Settings> content to an XML object
[xml]$newSettingsXml = $newSettingsContent

# Find all <Settings> nodes in the XML file
$targetNodes = $xml.SelectNodes("//Settings")

if ($targetNodes.Count -gt 0) {
    foreach ($node in $targetNodes) {
        # Remove existing child nodes inside <Settings>
        $node.RemoveAll()
        
        # Add new subchild nodes from the input
        foreach ($newChild in $newSettingsXml.Settings.ChildNodes) {
            $importedNode = $xml.ImportNode($newChild, $true)
            $node.AppendChild($importedNode) | Out-Null
        }
    }

    # Save the modified XML back to the file
    $xml.Save($xmlFile)
    Write-Output "XML updated successfully."
} else {
    Write-Output "No <Settings> tag found."
}


---

Example Input (config.xml before execution):

<Root>
    <ModuleA>
        <Config>
            <Settings>
                <Option1>OldValue1</Option1>
                <Option2>OldValue2</Option2>
                <IgnoreThis>DoNotChange</IgnoreThis>
            </Settings>
        </Config>
    </ModuleA>
    <ModuleB>
        <Settings>
            <OptionA>OldValueA</OptionA>
            <OptionB>OldValueB</OptionB>
        </Settings>
    </ModuleB>
</Root>

New Input for <Settings>:

<Settings>
    <Option1>NewValue1</Option1>
    <Option2>NewValue2</Option2>
</Settings>


---

Output after script execution (config.xml after execution):

<Root>
    <ModuleA>
        <Config>
            <Settings>
                <Option1>NewValue1</Option1>
                <Option2>NewValue2</Option2>
            </Settings>
        </Config>
    </ModuleA>
    <ModuleB>
        <Settings>
            <Option1>NewValue1</Option1>
            <Option2>NewValue2</Option2>
        </Settings>
    </ModuleB>
</Root>


---

How It Works:

1. Reads the XML file and finds all <Settings> nodes at any depth.


2. 

