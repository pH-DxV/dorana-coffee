$base = "dorana\backend\src\main\java\br\unitins\topicos1\lgc"

$map = [ordered]@{
    "Administrador" = "administrador"
    "Auth" = "auth"
    "Cafe" = "cafe"
    "CategoriaDoCafe" = "categoriaDoCafe"
    "CategoriaSensorial" = "categoriaSensorial"
    "Cliente" = "cliente"
    "DefaultEntity" = "defaultEntity"
    "Endereco" = "endereco"
    "Estado" = "estado"
    "Estoque" = "estoque"
    "Exception" = "exception"
    "Frete" = "frete"
    "Hash" = "hash"
    "ItemPedido" = "itemPedido"
    "JwT" = "jwt"
    "Marca" = "marca"
    "Municipio" = "municipio"
    "NivelDeTorra" = "nivelDeTorra"
    "NotaSensorial" = "notaSensorial"
    "Pagamento" = "pagamento"
    "Pedido" = "pedido"
    "Perfil" = "perfil"
    "Regiao" = "regiao"
    "RegiaoDeOrigem" = "regiaoDeOrigem"
    "Security" = "security"
    "Telefone" = "telefone"
    "Tratamento" = "tratamento"
    "Usuario" = "usuario"
}

foreach ($entry in $map.GetEnumerator()) {
    $oldName = $entry.Key
    $newName = $entry.Value
    $oldPath = Join-Path $base $oldName
    $tmpPath = Join-Path $base ($newName + "_tmp")
    $finalPath = Join-Path $base $newName

    if (Test-Path $oldPath) {
        Write-Output "Renomeando $oldName -> $newName"
        git mv $oldPath $tmpPath
        git mv $tmpPath $finalPath
    }
}
