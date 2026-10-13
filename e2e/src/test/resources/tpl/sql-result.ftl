<html>
<#-- @ftlvariable name="data" type="mipkasnow.dev.rococo.data.logging.SqlResultAttachment" -->
<head>
    <meta http-equiv="content-type" content="text/html; charset = UTF-8">
    <link href="https://yastatic.net/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
</head>
<body>

<h5>SQL Result: ${data.rowCount} rows</h5>

<#if data.headers?has_content>
    <table class="table table-bordered table-condensed">
        <thead>
        <tr>
            <#list data.headers as header>
                <th>${header?html}</th>
            </#list>
        </tr>
        </thead>
        <tbody>
        <#list data.rows as row>
            <tr>
                <#list row as value>
                    <td>${value?html}</td>
                </#list>
            </tr>
        </#list>
        </tbody>
    </table>
</#if>

</body>
</html>
