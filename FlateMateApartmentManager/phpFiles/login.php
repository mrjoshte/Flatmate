<?php

$post = json_decode(file_get_contents("php://input"), true);

$servername = '10.25.71.66';
$username = 'dbu309grp40';
$password = 'ujhKhRhjWQq';
$dbname = 'db309grp40';

$uname=$post['UserId'];
$upass=$post['Password'];

$checkUser = "SELECT UserId, Password, Email, FlatID, FirstName, LastName FROM User Where UserId = '$uname' and Password = '$upass'";
$checkIfTenant = "SELECT UserID FROM Tenant where UserId = '$uname'";
$checkIfLandlord = "SELECT LandlordId FROM Landlord where LandlordId = '$uname'";
$checkIfAdmin = "SELECT UserID FROM Admin where UserID = '$uname'";

$success = FALSE;
$role = "";
$flatID = "";
$firstName = "";
$lastName = "";

$conn = mysqli_connect($servername, $username, $password, $dbname);

if (!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

$result = $conn->query($checkUser);

if($result->num_rows > 0)
{
	$row = $result->fetch_assoc();
	$GLOBALS['email'] = $row["Email"];
	$flatID = $row['FlatID'];
	$firstName = $row['FirstName'];
	$lastName = $row['LastName'];
	
	if($row["UserId"] == $uname && $row["Password"] == $upass)
	{
		$success = TRUE;
	}	
	
	if($conn->query($checkIfTenant)->num_rows > 0)
	{
		$role = "Tenant";
	}
	if($conn->query($checkIfAdmin)->num_rows > 0)
	{
		$role = "Admin";
	}
	if($conn->query($checkIfLandlord)->num_rows > 0)
	{
		$role = "Landlord";
	}
}

//$arr = array('Success' => $success, 'Role' => $role);

$arr = array('Success' => $success, 'Role' => $role, 'email' => $GLOBALS['email'], 'flatID' => $flatID, 'firstName' => $firstName, 'lastName' => $lastName, 'remote_user' => $_SERVER['REMOTE_ADDR']);

$conn->close();

echo json_encode($arr);
?>
