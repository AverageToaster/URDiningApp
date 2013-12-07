<?php

if (isset($_GET['id'])) {
    $db = new SQLite3('/home/ryanpuff/public_html/dining/dining.sqlite');
    $stmt = $db->prepare("UPDATE events SET validated=1 WHERE valkey=:valid;");
    $stmt->bindParam(":valid", $_GET['id']);
    $stmt->execute();
    $db = null;
}

header('Location: index.php');

?>
