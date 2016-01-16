<?php

$servername = '10.25.71.66';
$username = 'dbu309grp40';
$password = 'ujhKhRhjWQq';
$dbname = 'db309grp40';

$conn = mysqli_connect($servername, $username, $password, $dbname);

if (!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

$post = json_decode(file_get_contents("php://input"), true);

$code = $post['code'];
$to = $post['to'];

$subject = 'You have been invited to an Apartment!';
$message = 'You have been invited to an apartment by a fellow roommate!' . PHP_EOL . 'Use this Email when creating your account to successfully join.' . PHP_EOL . PHP_EOL . 'Apartment Code: ' . $code;
$headers = 'From: Flat Mate Apartment Manager <DoNotReply@iastate.edu>';

mail($to, $subject, $message, $headers);

$sql = "INSERT INTO Invited values ('$to','$code')";

$success = "Fail";

if ($conn->query($sql) === TRUE) {
	$success = "success";
}

//Insert into RecentActivity
$sql = "INSERT into RecentActivity values ('$code', '$to has been invited to the Flat!', 'Flat')";
if($conn->query($sql) === FALSE)
{
	$success = "Fail";
}

echo json_encode(array('a' => $success));
?>