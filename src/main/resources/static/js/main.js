// Get the row data clicked intended to be modified
function clickRow(x) {
    alert("Row Data: " + x.parentNode.parentNode.childNodes[1].innerHTML)
    clearData()
}

// Make an AJAX Call to clear block infomration
async function clearData() {

    var url = new URL("http://127.0.0.1:8080/clear_info_block"), params = {num:1, transaction:0}
    Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))

    console.log(url) // TEST

    fetch(url)
        .then(function (res) {
            console.log(res);
        })
}

// Subimt a Transaction to the Network:
async function submitTX() {

    // get the form input data:
    var r_address = document.getElementById("r_address").value;
    var amount = document.getElementById("amount").value;
    var msg = document.getElementById("msg").value;

    var url = new URL("http://127.0.0.1:8080/send_funds"), params = {funds:amount, msg:msg}
    Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))

    console.log(url) // TEST

    fetch(url)
        .then(function (res) {
            console.log(res);
        })
}