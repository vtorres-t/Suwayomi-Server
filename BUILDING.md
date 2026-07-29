# Command (PowerShell) remove commentaries and order xml "string.xml" ==>> "string_ordered.xml"

cd \server\i18n\src\commonMain\moko-resources\values\base

cd \server\i18n\src\commonMain\moko-resources\values\es

[xml]$xml = Get-Content "strings.xml" -Raw; $ordenados = $xml.resources.string | Sort-Object name; $xml.resources.InnerXml = ""; $ordenados | ForEach-Object { [void]$xml.resources.AppendChild($xml.ImportNode($_, $true)) }; $xml.Save("$pwd\strings_ordenado.xml")
