<?php

//session_start();

//$_SESSION["username"] = "uc1";

$data = json_decode(file_get_contents('posts.txt'), true);
//echo $data;
foreach($data as $key => $val){
   echo $key . " " . $val;
}

			
?>