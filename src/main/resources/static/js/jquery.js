$(document).ready(function () {
    $("#select_tx_btn").click(function () {
        var $row = $(this).closest("tr");                   // Find the row
        var $text = $row.find("td:nth-child(2)").text();    // Find the text

        alert($text);                                       // Get Certain Text
    });
});