<?php
//decoding the json array
$post = json_decode(file_get_contents("php://input"), true);

$FlatID = $post['FlatID'];

//getting the information from the array
//in the android example I've defined only one KEY. You can add more KEYS to you app

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

$sql = "SELECT FirstName, LastName FROM User where FlatID = '$FlatID'";


$result = mysqli_query($conn, $sql); 

$yourArray = array();

while($row = mysqli_fetch_assoc($result))
{
	$yourArray[]=$row;
}

$conn->close();

echo json_encode(["list" => $yourArray]);
?>
