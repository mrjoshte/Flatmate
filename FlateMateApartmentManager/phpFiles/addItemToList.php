<?php

$post = json_decode(file_get_contents("php://input"), true);

$servername = '10.25.71.66';
$username = 'dbu309grp40';
$password = 'ujhKhRhjWQq';
$dbname = 'db309grp40';

$itemName=$post['itemName'];
$listName=$post['ListName'];
$selected=$post['Selected'];
$flatID=$post['FlatID'];

$itemName = addslashes($itemName);

//create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);
//check connection
if(!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

$returnString = 'Fail';

$sql = "INSERT INTO ListItems values ('$itemName', '$listName', '$selected', '$flatID')";

if ($conn->query($sql) === TRUE)
{
	$returnString = 'Success';
}

//Insert into RecentActivity
$sql = "INSERT into RecentActivity values ('$flatID', '$itemName has been added to $listName!', 'List')";
if($conn->query($sql) === FALSE)
{
	$success = "Fail";
}

$conn->close();

$arr = array('Success' => $returnString);

echo json_encode($arr);
?>
