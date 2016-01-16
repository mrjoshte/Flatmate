<?php

$post = json_decode(file_get_contents("php://input"), true);

$servername = '10.25.71.66';
$username = 'dbu309grp40';
$password = 'ujhKhRhjWQq';
$dbname = 'db309grp40';

$flatID=$post['FlatID'];
$expenseName=$post['ExpenseName'];
$expenseDescription=$post['ExpenseDescription'];
$assignedTo=$post['AssignedTo'];
$cost=$post['Cost'];
$expenseType=$post['ExpenseType'];

$expenseName = addslashes($expenseName);
$expenseDescription = addslashes($expenseDescription);

//create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);
//check connection
if(!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

$returnString = 'Fail';

$sql = "INSERT INTO Expenses values ('$flatID', '$expenseName', '$expenseDescription', '$assignedTo', '$cost', '$expenseType')";

if ($conn->query($sql) === TRUE)
{
	$returnString = 'Success';
}

//Insert into RecentActivity
$sql = "INSERT into RecentActivity values ('$flatID', 'A new $expenseType expense, $expenseName, has been created!', 'Expenses')";
if($conn->query($sql) === FALSE)
{
	$success = "Fail";
}

$conn->close();

$arr = array('Success' => $returnString);

echo json_encode($arr);
?>
