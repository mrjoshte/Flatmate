<?php

$post = json_decode(file_get_contents("php://input"), true);

$servername = '10.25.71.66';
$username = 'dbu309grp40';
$password = 'ujhKhRhjWQq';
$dbname = 'db309grp40';

$flatID=$post['FlatID'];

//create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);
//check connection
if(!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

$sql = "SELECT ListName from Lists WHERE FlatID = '$flatID'";

$result = mysqli_query($conn, $sql);

$yourArray = array();

while($row = mysqli_fetch_assoc($result))
{
	$yourArray[]=$row;
}

$conn->close();

echo json_encode(["list" => $yourArray]);

?>