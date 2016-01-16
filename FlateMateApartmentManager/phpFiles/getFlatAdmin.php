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

$sql = "SELECT AdminID from Flat WHERE FlatID = '$flatID'";
$result = mysqli_query($conn, $sql);

$row = $result->fetch_assoc();

$adminId = $row['AdminID'];

$conn->close();

$arr = array('AdminID' => $adminId );

echo json_encode($arr);

?>