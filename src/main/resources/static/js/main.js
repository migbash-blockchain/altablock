// -------------------------
//      Page Listeners
// -------------------------

var path_url = document.URL;

if (path_url.includes('/block_explorer')) {

    // -------------------------
    // Page UI/UX Changes
    // -------------------------

    document.getElementsByTagName('footer')[0].getElementsByTagName('a')[1].style.color = '#00A3FF'

    // -------------------------
    // Event Listeners
    // -------------------------

    document.getElementById('simple_view_btn').addEventListener('click', switchLayout)
    document.getElementById('visual_view_btn').addEventListener('click', switchLayout)
    document.getElementById('search_matrix').addEventListener('click', searchData)

    var btn_query_arr = document.getElementsByClassName('_btn_query')

    for (var i = 0; i < btn_query_arr.length; i++) {
        btn_query_arr[i].addEventListener('click', function () {
            changeInputTxt(this)
        })
    }

    // -------------------------
    // Initializing functions on "page load"
    // -------------------------

    getMatrixData()
    getBlockData()
    getTxData()

} else if (path_url.includes('/wallet')) {

    // -------------------------
    // Page UI/UX Changes
    // -------------------------

    document.getElementById('_btn_div_action_txt').innerHTML = `<p> hello </p>`
    document.getElementsByTagName('footer')[0].getElementsByTagName('a')[0].style.color = '#00A3FF'
    
    var rows_arr = document.getElementsByTagName('tr')

    for (let i = 0; i < rows_arr.length; i++) {
        if (i > 2) {
            rows_arr[i].classList.toggle('display_row');
        }
    }

    // -------------------------
    // Event Listeners
    // -------------------------

    document.getElementById('view_keys').addEventListener('click', submitTX)
    document.getElementById('new_wallet').addEventListener('click', createNewWallet)
    document.getElementById('access_wallet').addEventListener('click', submitTX)
    document.getElementById('sendTx').addEventListener('click', submitTX)
    document.getElementById('show_more_btn').addEventListener('click', showMoreTx)

    document.getElementById('cpy_pub_add').addEventListener('click', function () {
        copyClipboard(this)
    })

    document.getElementById('select_tx_btn').addEventListener('click', function () {
        clearData(this)
    })

    document.getElementById('user_action_btn').addEventListener('mouseout', function () {
        document.getElementById('_btn_div_action_txt').style.visibility = 'hidden'
    })

    var btns_arr = document.getElementsByClassName('_btn_div')

    for (var i = 0; i < btns_arr.length; i++) {
        btns_arr[i].addEventListener('mouseover', function () {
            showBtnFunc(this)
        })
    }
}

// -------------------------
// Simple UI/UX Functions Triggers
// -------------------------

function showMoreTx() {
    var rows_arr = document.getElementsByTagName('tr')

    for (let i = 0; i < rows_arr.length; i++) {
        if (i > 2) {
            rows_arr[i].classList.toggle('display_row');
        }
    }
}

function changeInputTxt(x) {
    var btnSelected_txt = x.getAttribute('title')
    document.getElementById('input_select').placeholder = btnSelected_txt
    if (btnSelected_txt == 'enter block number') {
        document.getElementsByClassName('_btn_query')[0].classList.add('selected')
        document.getElementsByClassName('_btn_query')[1].classList.remove('selected')
    } else {
        document.getElementsByClassName('_btn_query')[0].classList.remove('selected')
        document.getElementsByClassName('_btn_query')[1].classList.add('selected')
    }
}

function showBtnFunc(x) {
    var btnFunc_txt = x.getAttribute('title')
    document.getElementById('_btn_div_action_txt').innerHTML = `<p> ${btnFunc_txt} </p>`
    document.getElementById('_btn_div_action_txt').style.visibility = 'visible'
}

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

// -------------------------
// AJAX/ASYNC Functions
// -------------------------

async function searchData() {

    // Validate Pressed Button for search
    var btn_select = document.getElementsByClassName('_btn_query')[0].classList.contains('selected')
    var input = document.getElementById('input_select').value

    // True?
    if (btn_select) {

        var url = new URL("/get_block_transactions", document.URL),
            params = {
                block_num: input,
            }
        Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))

        window.open(url)
    } else {

        var url = new URL("/get_transaction", document.URL),
            params = {
                tx_id: input,
            }
        Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))

        window.open(url)
    }


}

async function submitTX() {

    // get the form input data:
    var r_address = document.getElementById("r_address").value;
    var amount = document.getElementById("amount").value;
    var msg = document.getElementById("msg_text").textContent;

    var url = new URL("/send_funds", document.URL),
        params = {
            funds: amount,
            msg: msg
        }
    Object.keys(params).forEach(key => url.searchParams.append(key, params[key]))

    fetch(url)
        .then((res) => res.text())
        .then((data) => {
            alert(data)
        })
        .catch((err) => console.log(err))
}

/**
 * 
 * @param {*} x 
 */
async function clearData(x) {

    var block_num = x.parentNode.parentNode.getElementsByTagName("td")[5].innerHTML
    // var transaction_num = x.parentNode.parentNode.getElementsByTagName("strong")[5].innerHTML
    var new_info = "null"

    var url = new URL("/modify_transaction_info", document.URL),
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

/**
 * TODO: [AJAX] [ASYNC] - Generate New Matrix Wallet
 * 
 * Using [Fetch API]
 * 
 * Process:
 * 
 * - Generate a new Instance of the Wallet,
 * - Pass New Wallet Parameters to the user
 * 
 */
async function createNewWallet() {

    fetch('/generate_new_wallet')
        .then((res) => res.text())
        .then((data) => {
            $('#staticBackdrop').modal('toggle')
            document.getElementById("wallet_info").innerHTML = data
        })
        .catch((err) => console.log(err))
}

/**
 * TODO: [AJAX] - Update BlockMatrix Data
 * _______
 * Using [Fetch API]
 * _______
 * Process:
 * 
 * - Get all of the updated blockmatrix DATA as a JSON Object
 * - Populate target html DOM elements with respective data;
 * - Repeat every 1 min (60s)
 */
async function getMatrixData() {

    fetch('/get_blockmatrix')
        .then((res) => res.text())
        .then((data) => {
            var result = JSON.parse(data);
            document.getElementById('countBlock_data').textContent = result.block_count
            document.getElementById('modBlock_data').textContent = result.block_mod_count
            document.getElementById('tx_data').textContent = result.tx_count
        })
        .catch((err) => console.log(err))

    setTimeout(getMatrixData, 60000);
}

/**
 * TODO: [AJAX] - Update BLockMatrix Table Data
 * _______
 * Using [Fetch API]
 * _______
 * Process:
 */
async function getTxData() {

    var table = document.getElementById('bt_tx_table');

    fetch('/get_blockmatrix_transactions')
        .then((res) => res.text())
        .then((data) => {
            var result = JSON.parse(data).replace(/\\/g, "")
            document.getElementById('contents').innerHTML = ''
            for (var tx_data of result) {
                var row = document.getElementById('contents').insertRow(0);
                row.insertCell(0).innerHTML = tx_data.transactionId
                row.insertCell(1).innerHTML = tx_data.sender
                row.insertCell(2).innerHTML = tx_data.recipient
                row.insertCell(3).innerHTML = tx_data.value
                row.insertCell(4).innerHTML = tx_data.blockNumber
                row.insertCell(5).innerHTML = timeConverter(tx_data.timeStamp)
            }
        })
        .catch((err) => console.log(err))

    setTimeout(getTxData, 60000);
}

/**
 * TODO: [AJAX] - Update Block 'Visual' Data
 * _______
 * Using [Fetch API]
 * _______
 * Process:
 * 
 * - Get all of the updated blockmatrix DATA as a JSON object,
 * - Populate target Table with the updated data,
 * - Repeat every 1 min (60s)
 * 
 */
async function getBlockData() {

    var main_div = document.getElementById('visual_view_div')
    var i = 0

    fetch('/get_blockmatrix_blocks')
        .then((res) => res.text())
        .then((data) => {
            main_div.innerHTML = ""
            var result = JSON.parse(data)
            for (var block_data of result) {
                i++
                main_div.innerHTML +=
                    `<div class="out_block_div">
                    <h6> Block #${i} </h6>
                    <div class="div_block">
                        <ul>
                            <li class="overflow_style"> Block Hash ${block_data.hash} </li>
                            <li> TimeStamp ${timeConverter(block_data.timeStamp)} </li>
                            <li> Tx’s ${block_data.transactions.length} </li>
                        </ul>
                    </div>
                </div>`;
            }
        })
        .catch((err) => console.log(err))

    setTimeout(getBlockData, 60000);
}

/**
 * [Helper Function]
 * _____
 * Desc:
 * 
 */
function timeConverter(_timestamp) {

    var a = new Date(_timestamp);
    var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    var year = a.getFullYear();
    var month = months[a.getMonth()];
    var date = a.getDate();
    var hour = a.getHours();
    var min = a.getMinutes();
    var sec = a.getSeconds();
    var time = date + ' ' + month + ' ' + year + ' ' + hour + ':' + min + ':' + sec;

    return time;
}