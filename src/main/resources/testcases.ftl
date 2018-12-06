<html>
<head>
  <meta charset="utf-8">
  <title>Test Cases</title>
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
        integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
</head>
<body>
<div class="content">
  <div class="container">
    <h1>Test Cases</h1>
  <#list data as testcase>
  <h2><span class="badge badge-primary">${testcase.id}</span> ${testcase.name}</h2>
  <dl>
    <dt>Features</dt>
    <dd>${testcase.features?join(", ")}</dd>
    <dt>Stories</dt>
    <dd>${testcase.stories?join(", ")}</dd>
  </dl>
  <h3>Scenario</h3>
  <ol>
  <#list testcase.steps as step>
    <li>${step.name}</li>
  </#list>
  </ol>
  </#list>
  </div>
</div>

</body>
</html>
