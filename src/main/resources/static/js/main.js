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

    fetch(url)
        .then((res) => res.text())
        .then((data) => {
            $('#staticBackdrop').modal('toggle')
            document.getElementById("wallet_info").innerHTML = data
            // alert(data);
        })
        .catch((err) => console.log(err))
}

// Updating Visual Block Explorer Table Data
async function getMatrix() {

    //TODO: Fetch BlockData for the existing blocks on the matrix

    var main_div = document.getElementById('block_view')
    
    var url = new URL("http://127.0.0.1:8080/get_matrix_block_num")

    fetch(url)
        .then((res) => res.text())
        .then((data) => {
            main_div.innerHTML = ""
            for(i = 0; i < data.valueOf(); i++){
                var new_block = document.createElement('div')
                new_block.setAttribute('class', 'div_block')
                new_block.innerHTML = i
                main_div.appendChild(new_block)
            }
        })
        .catch((err) => console.log(err))
    
    setTimeout(getMatrix, 1000);
}