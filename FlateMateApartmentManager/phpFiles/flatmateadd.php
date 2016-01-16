<?php

$post = json_decode(file_get_contents("php://input"), true);
$First = $post['FirstName'];
$Last = $post['LastName'];
$Password = $post['Password'];
$UserId = $post['UserId'];
$Email = $post['Email'];
$DOB = $post['DOB'];


$servername = '10.25.71.66';
$username = 'dbu309grp40';
$password = 'ujhKhRhjWQq';
$dbname = 'db309grp40';

$conn = mysqli_connect($servername, $username, $password, $dbname);

if (!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

$sql = "INSERT INTO User
Values('$First', '$Last', '$Password', '$UserId', '$Email', '$DOB', '')";

$returnString = null;

if ($conn->query($sql) === TRUE) {
    $returnString = "New record created successfully";
} else {
    $returnString = "Username already exists. Please choose another.";
}
$conn->close();
$arr = array('Success' => $returnString);
echo json_encode($arr);
?>
