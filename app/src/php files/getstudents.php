<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
require "db.php";

$sql = "SELECT id, roll_no, name, email, phone, department, year FROM users WHERE user_type = 'student' ORDER BY name ASC";
$result = $conn->query($sql);

$students = [];
while ($row = $result->fetch_assoc()) {
    $students[] = [
        "id" => (int)$row['id'],
        "rollNo" => $row['roll_no'],
        "name" => $row['name'],
        "email" => $row['email'],
        "phone" => $row['phone'] ?? "",
        "department" => $row['department'] ?? "",
        "year" => $row['year'] ?? ""
    ];
}

echo json_encode([
    "status" => true,
    "students" => $students
]);

$conn->close();
?>
