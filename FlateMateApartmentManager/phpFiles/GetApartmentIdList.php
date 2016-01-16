<?php
//decoding the json array
$post = json_decode(file_get_contents("php://input"), true);

$uname=$post['LandlordId'];
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

$sql = "SELECT ApartmentID from db309grp40.Apartment WHERE LandlordId In('jj')";



//$result = $conn->query($sql) or die (mysqli_error());
$result = mysqli_query($conn, $sql);

$yourArray = array();

$index = 0;
while($row = mysqli_fetch_assoc($result))
{
	$yourArray["$index"] = $row["ApartmentID"];

	$index = $index + 1;
}

$conn->close();
$yourArray['size'] = $index;
//$ourArray = array('size' => TRUE);

echo json_encode($yourArray);
?>
