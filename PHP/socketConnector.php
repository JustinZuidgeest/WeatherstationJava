<?php

$address = "localhost";
$port = 54872;

//Attempt to create a socket resource and echo an error code if the attempt fails
if (($socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP)) === false) {
  echo "socket creation failed: " .socket_strerror(socket_last_error())."\n";
}

//Attempt to establish socket connection and echo an error code if the attempt fails
if (($connection = socket_connect($socket, $address, $port)) === false){
  echo "Socket connection failed: ".socket_strerror(socket_last_error())."\n";
}


$input = "update\r\n";

socket_write($socket, $input, strlen($input));

$result = socket_read($socket, 4096, PHP_NORMAL_READ);

// Split into rows
$entries = explode(";", $result);
$aso = [];

// Extract header
$header = explode(",", array_shift($entries));

// Create associative array 
foreach($entries as $entry) {
  $arr = explode(",", $entry);
  array_push($aso, array_combine($header, explode(",", $entry)));
}

// Print result
foreach($aso as $row) {
  print_r($row);
  echo "<br><br>";
}

$input = "close\n";

socket_write($socket, $input, strlen($input));

socket_close($socket);

?>