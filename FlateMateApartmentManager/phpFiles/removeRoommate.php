<?php
//decoding the json array
$post = json_decode(file_get_contents("php://input"), true);

$flatID=$post['FlatID'];
$userID=$post['UserID'];
$landlordID=$post['landlordID'];

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

$success = "False";

$sql = "delete from Tenant where UserID = '$userID'";
if($conn->query($sql) === TRUE)
{
	$sql = "UPDATE User SET FlatID='' WHERE UserId='$userID'";
	if($conn->query($sql) === TRUE)
	{
		$success = "Success";
	}
}

if($landlordID != "")
{
	$sql = "delete from Admin where UserID = '$userID'";

	if($conn->query($sql) === TRUE)
	{
		$sql = "UPDATE User SET FlatID='' WHERE UserId='$userID'";
		if($conn->query($sql) === TRUE)
		{
			$success = "Success";
		}
	}
}

//Remove flat Id from user
$sql = "UPDATE User SET FlatID='' WHERE UserId='$userID'";
if($conn->query($sql) === FALSE)
{
	$success = "Fail";
}

//Get the users email so we can remove him from the invited table
$sql = "SELECT Email, FirstName, LastName from User where UserId = '$userID'";
if($conn->query($sql) === FALSE)
{
	$success = "Fail";
}
else
{
	$result = $conn->query($sql);
	$row = $result->fetch_assoc();
	$email = $row['Email'];
	$firstName = $row['FirstName'];
	$lastName = $row['LastName'];
}

//Remove user from invited table so he/she can't join again
$sql = "Delete from Invited where FlatID = '$flatID' and Email = '$email'";
if($conn->query($sql) === FALSE)
{
	$success = "Fail";
}

//Insert into RecentActivity
$sql = "INSERT into RecentActivity values ('$flatID', '$firstName $lastName has been kicked out of the Flat!', 'Flat')";

if($conn->query($sql) === FALSE)
{
	$success = "Fail";
}

$conn->close();

$arr = array('Success' => $success);

echo json_encode($arr);
?>