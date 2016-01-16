<?php
//decoding the json array
$post = json_decode(file_get_contents("php://input"), true);

$flatID=$post['FlatID'];
$adminID=$post['AdminID'];
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

$success = "False";

$sql = "delete from Tenant where UserID = '$userID'";
	if($conn->query($sql) === TRUE)
	{
		$sql = "insert into Admin values ('$userID')";
		if($conn->query($sql) === TRUE)
		{
			$sql = "UPDATE Flat SET AdminID = '$userID' WHERE FlatID = '$flatID'";
			if($conn->query($sql) === TRUE)
			{
				$sql = "delete from Admin where UserID = '$adminID'";
				if($conn->query($sql) === TRUE)
				{
					$sql = "insert into Tenant values ('$adminID')";
					if($conn->query($sql) === TRUE)
					{
						$success = "Success";
					}
				}
			}
			else
			{
				$sql= "delete from Admin where UserID = '$userID'";
				$conn->query($sql);
				$success = "Fail";
				
				//Insert into RecentActivity
				$sql = "INSERT into RecentActivity values ('$flatID', '$adminID gave their admin rights to $userID!', 'Flat')";
				if($conn->query($sql) === FALSE)
				{
					$success = "Fail";
				}
			}
		}
	}
	
$conn->close();

$arr = array('success' => $success);

echo json_encode($arr);
?>