<?php
//decoding the json array
$post = json_decode(file_get_contents("php://input"), true);

$flatID=$post['FlatID'];
$userID=$post['UserID'];

$servername = '10.25.71.66';
$username = 'dbu309grp40';
$password = 'ujhKhRhjWQq';
$dbname = 'db309grp40';

//create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);
//check connection
if(!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

$sql = "SELECT FirstName, LastName, UserId from User where FlatID = '$flatID' and UserId != '$userID'";

$result = mysqli_query($conn, $sql);

$yourArray = array();

while($row = mysqli_fetch_assoc($result))
{
	$yourArray[] = $row;
}

$conn->close();

echo json_encode(["list" => $yourArray]);
?>