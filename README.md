bGot it! You need a PowerShell script that finds a particular tag (Settings) at any depth within the XML and updates only its subchild elements while preserving other parts of the XML.


---

PowerShell Script:

# Define the XML file path
$xmlFile = "C:\path\to\config.xml"

# Load the XML
[xml]$xml = Get-Content $xmlFile

# Find all occurrences of the specific tag at any depth
$targetNodes = $xml.SelectNodes("//Settings")

if ($targetNodes.Count -gt 0) {
    foreach ($node in $targetNodes) {
        # Modify only the subchild elements inside each <Settings> tag
        foreach ($child in $node.ChildNodes) {
            $child.InnerText = "Updated_" + $child.Name  # Modify the value
        }
    }

    # Save the modified XML back
    $xml.Save($xmlFile)
    Write-Output "XML updated successfully."
} else {
    Write-Output "No <Settings> tag found."
}


---

Example Input (config.xml):

<Root>
    <ModuleA>
        <Config>
            <Settings>
                <Option1>Value1</Option1>
                <Option2>Value2</Option2>
            </Settings>
        </Config>
    </ModuleA>
    <ModuleB>
        <Settings>
            <OptionA>ValueA</OptionA>
            <OptionB>ValueB</OptionB>
        </Settings>
    </ModuleB>
</Root>

Output after script execution (config.xml):

<Root>
    <ModuleA>
        <Config>
            <Settings>
                <Option1>Updated_Option1</Option1>
                <Option2>Updated_Option2</Option2>
            </Settings>
        </Config>
    </ModuleA>
    <ModuleB>
        <Settings>
            <OptionA>Updated_OptionA</OptionA>
            <OptionB>Updated_OptionB</OptionB>
        </Settings>
    </ModuleB>
</Root>


---

How It Works:

1. The script searches for all <Settings> tags, no matter how deeply nested they are.


2. It loops through each <Settings> node and updates only its direct subchild elements.


3. The rest of the XML structure remains unchanged.


4. The modified XML is saved back to the file.



Let me know if you need further refinements!

