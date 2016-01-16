<?php

$post = json_decode(file_get_contents("php://input"), true);

$servername = '10.25.71.66';
$username = 'dbu309grp40';
$password = 'ujhKhRhjWQq';
$dbname = 'db309grp40';

$uname=$post['UserID'];
$upass=$post['Password']; 
$email=$post['Email'];
$firstName=$post['FirstName'];
$lastName=$post['LastName'];

$conn = mysqli_connect($servername, $username, $password, $dbname);

if (!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

$success = 'Fail';

$checkIfInvited = "SELECT Email, FlatID from Invited where Email = '$email' and FlatID = '$upass'";
$result = $conn->query($checkIfInvited);

if($result->num_rows > 0)
{
	$checkFlats = "SELECT FlatID FROM Flat Where FlatID = '$upass'";

	$result = $conn->query($checkFlats);

	if($result->num_rows > 0)
	{
		$addIDToUser = "UPDATE User 
			SET FlatID='$upass' 
			WHERE UserId='$uname'";
		if($conn->query($addIDToUser) === TRUE){
			$addIDToTenant = "INSERT INTO Tenant values ('$uname')";
			if($conn->query($addIDToTenant) === TRUE){
				$success = 'success';
			}
		}
	}
}

//Insert into RecentActivity
$sql = "INSERT into RecentActivity values ('$upass', '$firstName $lastName has joined the Flat!', 'Flat')";
if($conn->query($sql) === FALSE)
{
	$success = "Fail";
}

$conn->close();

$arr = array('Success' => $success);

echo json_encode($arr);
?>
