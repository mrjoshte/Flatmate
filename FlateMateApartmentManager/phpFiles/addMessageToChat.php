<?php

$post = json_decode(file_get_contents("php://input"), true);

$servername = '10.25.71.66';
$username = 'dbu309grp40';
$password = 'ujhKhRhjWQq';
$dbname = 'db309grp40';

$message=$post['Message'];
$flatID=$post['FlatID'];
$userID=$post['UserID'];
$firstName=$post['FirstName'];
$lastName=$post['LastName'];

$message = addslashes($message);

//create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);
//check connection
if(!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

$returnString = 'Fail';

date_default_timezone_set('America/Belize');
$date = (String)date("h:ia");
$sql = "INSERT INTO Chat values ('$flatID', '$message', '$userID', '$firstName', '$lastName', '$date')";

if ($conn->query($sql) === TRUE)
{
	$returnString = 'Success';
}

//Insert into RecentActivity
$sql = "INSERT into RecentActivity values ('$flatID', '$firstName $lastName posted in the chat!', 'Chat')";
if($conn->query($sql) === FALSE)
{
	$success = "Fail";
}

$conn->close();

$arr = array('Success' => $returnString);

echo json_encode($arr);
?>
