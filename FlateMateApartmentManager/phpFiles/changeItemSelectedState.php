<?php

$post = json_decode(file_get_contents("php://input"), true);

$servername = '10.25.71.66';
$username = 'dbu309grp40';
$password = 'ujhKhRhjWQq';
$dbname = 'db309grp40';

$itemName=$post['itemName'];
$listName=$post['ListName'];
$flatID=$post['FlatID'];
$selected=$post['Selected'];

//create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);
//check connection
if(!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

$returnString = 'Fail';

$sql = "UPDATE ListItems SET Selected = '$selected' WHERE Item = '$itemName' and ListName = '$listName' and FlatID = '$flatID'";

if ($conn->query($sql) === TRUE)
{
	$returnString = 'Success';
}

$conn->close();

$arr = array('Success' => $returnString);

echo json_encode($arr);
?>
