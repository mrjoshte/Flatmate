<?php
//decoding the json array
$post = json_decode(file_get_contents("php://input"), true);

//getting the information from the array
//in the android example I've defined only one KEY. You can add more KEYS to you app

$ApartmentID = $post['ApartmentID'];
$LandlordID = $post['LandLordID'];
$Address = $post['Address'];

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

$sql = "INSERT INTO Landlord values ('$Address','$LandlordID')";

if ($conn->query($sql) === FALSE) {
	//Do nothing, the landlord already belongs in the landlord table
}

$sql = "INSERT INTO Apartment values ('$Address','$ApartmentID','$LandlordID')";

$returnString = "Failed";

if ($conn->query($sql) === TRUE) {
	$returnString = "New record created successfully";
}



$conn->close();

$arr = array('a' => $returnString);

echo json_encode($arr);
?>
