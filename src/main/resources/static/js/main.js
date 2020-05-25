// _________________________
// UI/UX Functions
// _________________________

function copyClipboard(x) {
    var dummy = document.createElement("input");
    var copyText = x.getAttribute("alt");
    document.body.appendChild(dummy);
    dummy.value = copyText
    dummy.select()
    document.execCommand("copy");
    document.body.removeChild(dummy);
}

// _________________________
// AJAX / ASYNC Functions
// _________________________

// Make an AJAX Call to Modify TX info
async function clearData(x) {

    // get the selected row data
    var block_num = x.parentNode.parentNode.getElementsByTagName("strong")[5].innerHTML
    // var transaction_num = x.parentNode.parentNode.getElementsByTagName("strong")[5].innerHTML
    var new_info = "null"

    var url = new URL("http://127.0.0.1:8080/modify_transaction_info"),
        params = {
            num: block_num,
            transaction: 0,
            new_info: new_info
        }
    Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))

    fetch(url)
        .then(function (res) {
            console.log(res);
        })

    location.reload()
}

// Subimt a Transaction to the Network:
async function submitTX() {

    // get the form input data:
    var r_address = document.getElementById("r_address").value;
    var amount = document.getElementById("amount").value;
    var msg = document.getElementById("msg_text").textContent;

    var url = new URL("http://127.0.0.1:8080/send_funds"),
        params = {
            funds: amount,
            msg: msg
        }
    Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))

    fetch(url)
        .then(function (res) {
            console.log(res);
        })
}

// Generate New Matrix Wallet
async function createNewWallet() {

    var url = new URL("http://127.0.0.1:8080/generate_new_wallet")

    // fetch(url)
    //     .then(function (res) {
    //         console.log(res);
    //     })

    fetch(url)
        .then((res) => res.text())
        .then((data) => {
            $('#staticBackdrop').modal('toggle')
            // var modal = document.getElementById("staticBackdrop").modal("show")
            // modal.getElementByClassName("modal-body").innerHTML = data
            alert(data);
        })
        .catch((err) => console.log(err))
}