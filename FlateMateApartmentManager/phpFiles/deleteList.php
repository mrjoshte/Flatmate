<?php

$post = json_decode(file_get_contents("php://input"), true);

$servername = '10.25.71.66';
$username = 'dbu309grp40';
$password = 'ujhKhRhjWQq';
$dbname = 'db309grp40';

$listName=$post['ListName'];
$flatID=$post['FlatID'];

//create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);
//check connection
if(!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

$returnString = 'Fail';

$sql = "DELETE FROM ListItems where FlatID = '$flatID' and ListName = '$listName'";

if ($conn->query($sql) === TRUE)
{
	$returnString = 'Success';
}

$sql = "DELETE FROM Lists where ListName = '$listName' and FlatID = '$flatID'";

if ($conn->query($sql) === TRUE)
{
	$returnString = 'Success'; 
}
else
{
	$returnString = 'Fail';
}

//Insert into RecentActivity
$sql = "INSERT into RecentActivity values ('$flatID', '$listName has been deleted!', 'List')";
if($conn->query($sql) === FALSE)
{
	$success = "Fail";
}

$conn->close();

$arr = array('Success' => $returnString);

echo json_encode($arr);
?>
