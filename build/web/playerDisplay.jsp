<!doctype html>
<html lang="en">
    <head>
        <title>Player Info</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/PlayerDisplay.css"/>
    </head>
    <body>
        <h1>Placecraft+</h1>
        <p> Player Info</p>
        <form action="servlet/PlayerDisplay">
            <p>Search using your name here<br/><br/><input type="text" name="name"/><br/>
                <button type="submit" value="Search">Search</button></p>
        </form>
    </body>
</html>