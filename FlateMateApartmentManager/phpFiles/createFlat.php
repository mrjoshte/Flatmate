<?php
//decoding the json array
$post = json_decode(file_get_contents("php://input"), true);

//getting the information from the array
//in the android example I've defined only one KEY. You can add more KEYS to you app

$AdminID = $post['AdminID'];
$Address = $post['Address'];
$FlatID = $post['FlatID'];
$ApartmentID = $post['ApartmentID'];
$UnitName = $post['UnitName'];


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

$returnString = "Fail";

if(strcmp($ApartmentID, "") == 0)
{
	$sql = "INSERT INTO Admin values ('$AdminID')";

	if ($conn->query($sql) === TRUE) {
			$returnString = "Failed";

			$sql = "INSERT INTO Flat values ('$AdminID','$Address','$FlatID','$ApartmentID','$UnitName')";

			if ($conn->query($sql) === TRUE)
			{
				$sql = "UPDATE User 
					SET FlatID='$FlatID' 
					WHERE UserId='$AdminID'";
				if ($conn->query($sql) === TRUE) {
					$returnString = "New record created successfully";
				}
				else{
					$returnString = "Error: " . $sql . "\n" . $conn->error;
				}
			}
			else{
				$sql = "Delete from Admin 
					WHERE UserID='$AdminID'";
				$conn->query($sql);
				
				$returnString = "FlatID already exists";
			}
	}
}
else
{
	$sql = "INSERT INTO Flat values ('$AdminID','$Address','$FlatID','$ApartmentID','$UnitName')";

	if ($conn->query($sql) === TRUE)
	{
		$returnString = "New record created successfully";
	}
	else
	{
		$returnString = "FlatID already exists";
	}
}
$conn->close();

$arr = array('a' => $returnString);

echo json_encode($arr);
?>