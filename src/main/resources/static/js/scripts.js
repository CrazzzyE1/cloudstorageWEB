$(document).ready(function(){
    PopUpHide();
});
function deleteFolder(id){
    $("#delete_folder").show(id);
    document.getElementById("delete_id").value = id
}

function deleteFile(id){
    $("#delete_file").show(id);
    document.getElementById("deletef_id").value = id
}

function PopUpShowNewFolder(){
    $("#okno").show();
}
//Функция скрытия PopUp
function PopUpHide(){
    $("#delete_folder").hide();
    $("#delete_file").hide();
    $("#okno").hide();
}