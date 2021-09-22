<?php

include('connect.php');

	if($_SERVER['REQUEST_METHOD']=='POST')
	{
		$name=$_POST['name'];
		$description=$_POST['description'];
		$status=$_POST['status'];
		$username=$_POST['username'];

		$sql = "INSERT INTO task (name, description, status, username)
		VALUES ('$name', '$description', '$status', '$username')";

		if ($conn->query($sql) === TRUE)
		{
			echo "The task is added in the index : " .mysqli_insert_id($conn);
			// Returns the Auto-increment ID generated of the new record
		}
		else
		{
			echo "Error: " . $sql . "<br>" . $conn->error;
		}
	}
?>