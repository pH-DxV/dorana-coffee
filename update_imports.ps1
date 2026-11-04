$files = Get-ChildItem -Path "dorana\backend\src" -Recurse -Filter "*.java"

$map = @{
    "br.unitins.topicos1.lgc.Administrador" = "br.unitins.topicos1.lgc.administrador"
    "br.unitins.topicos1.lgc.Auth" = "br.unitins.topicos1.lgc.auth"
    "br.unitins.topicos1.lgc.Cafe" = "br.unitins.topicos1.lgc.cafe"
    "br.unitins.topicos1.lgc.CategoriaDoCafe" = "br.unitins.topicos1.lgc.categoriaDoCafe"
    "br.unitins.topicos1.lgc.CategoriaSensorial" = "br.unitins.topicos1.lgc.categoriaSensorial"
    "br.unitins.topicos1.lgc.Cliente" = "br.unitins.topicos1.lgc.cliente"
    "br.unitins.topicos1.lgc.DefaultEntity" = "br.unitins.topicos1.lgc.defaultEntity"
    "br.unitins.topicos1.lgc.Endereco" = "br.unitins.topicos1.lgc.endereco"
    "br.unitins.topicos1.lgc.Estado" = "br.unitins.topicos1.lgc.estado"
    "br.unitins.topicos1.lgc.Estoque" = "br.unitins.topicos1.lgc.estoque"
    "br.unitins.topicos1.lgc.Exception" = "br.unitins.topicos1.lgc.exception"
    "br.unitins.topicos1.lgc.Frete" = "br.unitins.topicos1.lgc.frete"
    "br.unitins.topicos1.lgc.Hash" = "br.unitins.topicos1.lgc.hash"
    "br.unitins.topicos1.lgc.ItemPedido" = "br.unitins.topicos1.lgc.itemPedido"
    "br.unitins.topicos1.lgc.JwT" = "br.unitins.topicos1.lgc.jwt"
    "br.unitins.topicos1.lgc.Marca" = "br.unitins.topicos1.lgc.marca"
    "br.unitins.topicos1.lgc.Municipio" = "br.unitins.topicos1.lgc.municipio"
    "br.unitins.topicos1.lgc.NivelDeTorra" = "br.unitins.topicos1.lgc.nivelDeTorra"
    "br.unitins.topicos1.lgc.NotaSensorial" = "br.unitins.topicos1.lgc.notaSensorial"
    "br.unitins.topicos1.lgc.Pagamento" = "br.unitins.topicos1.lgc.pagamento"
    "br.unitins.topicos1.lgc.Pedido" = "br.unitins.topicos1.lgc.pedido"
    "br.unitins.topicos1.lgc.Perfil" = "br.unitins.topicos1.lgc.perfil"
    "br.unitins.topicos1.lgc.Regiao" = "br.unitins.topicos1.lgc.regiao"
    "br.unitins.topicos1.lgc.RegiaoDeOrigem" = "br.unitins.topicos1.lgc.regiaoDeOrigem"
    "br.unitins.topicos1.lgc.Security" = "br.unitins.topicos1.lgc.security"
    "br.unitins.topicos1.lgc.Telefone" = "br.unitins.topicos1.lgc.telefone"
    "br.unitins.topicos1.lgc.Tratamento" = "br.unitins.topicos1.lgc.tratamento"
    "br.unitins.topicos1.lgc.Usuario" = "br.unitins.topicos1.lgc.usuario"
}

$c = 0
foreach ($file in $files) {
    $content = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::UTF8)
    $orig = $content

    foreach ($k in $map.Keys) {
        $target = $map[$k]
        if ($content.Contains($k)) {
            $content = $content.Replace($k, $target)
        }
    }

    if ($content -ne $orig) {
        [System.IO.File]::WriteAllText($file.FullName, $content, [System.Text.Encoding]::UTF8)
        $c++
    }
}

Write-Output "Arquivos alterados: $c"
