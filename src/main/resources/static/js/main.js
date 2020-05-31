// _________________________
// Event Listeners

document.getElementById('simple_view_btn').addEventListener('click', switchLayout);
document.getElementById('visual_view_btn').addEventListener('click', switchLayout);

// _________________________
// Page Listeners

var path_url = document.URL;

if (path_url.includes('/block_explorer')) {
    getMatrixData()
}

// _________________________
// Simple UI/UX Functions Triggers

function copyClipboard(x) {
    var dummy = document.createElement("input");
    var copyText = x.getAttribute("alt");
    document.body.appendChild(dummy);
    dummy.value = copyText
    dummy.select()
    document.execCommand("copy");
    document.body.removeChild(dummy);
}

function switchLayout() {

    document.getElementById('simple_view_div').classList.toggle('enabled')
    document.getElementById('visual_view_div').classList.toggle('enabled')
}


// _________________________
// AJAX / ASYNC UI/UX Functions

// Subimt a Transaction to the Network
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

// [AJAX] - Update BlockMatrix Data
async function getMatrixData() {

    fetch('/get_blockmatrix')
        .then((res) => res.text())
        .then((data) => {
            var result = JSON.parse(data);
            // document.getElementById('countBlock_data').textContent = result.block_count
            // document.getElementById('modBlock_data').textContent = result.block_mod_count
            // document.getElementById('tx_data').textContent = result.tx_count
            alert(result.txs_list.transactionId)
        })
        .catch((err) => console.log(err))

    setTimeout(getMatrixData, 60000);
}

// [AJAX] - Update BLockMatrix Data
async function getBlockData() {

    //TODO: Fetch BlockData for the existing blocks on the matrix

    var main_div = document.getElementById('visual_view_div')

    fetch('/get_matrix_block_num')
        .then((res) => res.text())
        .then((data) => {
            main_div.innerHTML = ""
            for (i = 0; i < data.valueOf(); i++) {
                var new_block = document.createElement('div')
                new_block.setAttribute('class', 'div_block')
                new_block.innerHTML = i
                main_div.appendChild(new_block)
            }
        })
        .catch((err) => console.log(err))

    // setTimeout(getVisualMatrixData, 1000);
}