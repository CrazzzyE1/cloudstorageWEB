$(document).ready(function () {
    PopUpHide();
});

function deleteFolder(id) {
    $("#delete_folder").show(id);
    document.getElementById("delete_id").value = id
}

function deleteFile(id) {
    $("#delete_file").show(id);
    document.getElementById("deletef_id").value = id
}

function PopUpShowNewFolder() {
    $("#okno").show();
}

function PopUpHide() {
    $("#delete_folder").hide();
    $("#delete_file").hide();
    $("#okno").hide();
    $("#rename_folder").hide();
    $("#rename_file").hide();
}

function rename(id) {
    $("#rename_folder").show();
    document.getElementById("rename_folder_id").value = id
}

function renamef(id) {
    document.getElementById("rename_file_id").value = id
    $("#rename_file").show();
}