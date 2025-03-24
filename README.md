 Understood! If you provide only the <Settings> part as input (instead of the entire XML file), the script will process and update only the subchild elements of <Settings>, keeping the rest untouched.


---

PowerShell Script:

# Input: Raw <Settings> XML content
$settingsXml = @"
<Settings>
    <Option1>OldValue1</Option1>
    <Option2>OldValue2</Option2>
    <IgnoreThis>DoNotChange</IgnoreThis>
</Settings>
"@

# Load the XML fragment
[xml]$xml = $settingsXml

# List of subchild tags that need to be updated
$tagsToUpdate = @("Option1", "Option2")  # Modify this list as needed

# Find the <Settings> node
$settingsNode = $xml.SelectSingleNode("Settings")

if ($settingsNode -ne $null) {
    foreach ($child in $settingsNode.ChildNodes) {
        # Update only the specified subchild elements
        if ($tagsToUpdate -contains $child.Name) {
            $child.InnerText = "Updated_" + $child.Name  # Modify the value
        }
    }

    # Output the modified <Settings> XML
    $xml.OuterXml
} else {
    Write-Output "No <Settings> tag found."
}


---

Example Input:

<Settings>
    <Option1>OldValue1</Option1>
    <Option2>OldValue2</Option2>
    <IgnoreThis>DoNotChange</IgnoreThis>
</Settings>

Output after script execution:

<Settings>
    <Option1>Updated_Option1</Option1>
    <Option2>Updated_Option2</Option2>
    <IgnoreThis>DoNotChange</IgnoreThis>
</Settings>


---

How It Works:

1. Takes the <Settings> XML as input.


2. Parses it and finds only the subchild elements inside <Settings>.


3. Updates only the specified tags (Option1, Option2 in this example).


4. Outputs the modified XML while keeping other subchild elements untouched.



Let me know if you need any modifications!

