//$.i18n( {locale: 'tr'} );
jQuery.loadScript = function (url, callback) {
    jQuery.ajax({
        url: url,
        dataType: 'script',
        success: callback,
        async: true
    });
}
var i18n = $.i18n();
console.log('/js/locales/lang-' + (i18n.locale !== 'tr' ? 'en' : i18n.locale) + '.json');
i18n.load( '/js/locales/lang-' + (i18n.locale !== 'tr' ? 'en' : i18n.locale) + '.json', i18n.locale ).done(
    function() {
        //alert($.i18n().locale + ' --- > ' + $.i18n( 'pass' ));
        console.log('body1 -- > i18n()');
        $('body').i18n();
        document.getElementById("filterApp").placeholder = $.i18n( 'app_name' );
        document.getElementById("serviceUserName").placeholder = $.i18n( 'service_pass' );
        document.getElementById("serviceUserPassword").placeholder = $.i18n( 'service_username' );
        document.getElementById("servicename").placeholder = $.i18n( 'service_name' );
        document.getElementById("serviceName").placeholder = $.i18n( 'service_name' );
        document.getElementById("headerkey1").placeholder = $.i18n( 'header_name' );
        document.getElementById("headervalue1").placeholder = $.i18n( 'header_value' );
        document.getElementById("operationName").placeholder = $.i18n( 'operation_name' );
        document.getElementById("path").placeholder = $.i18n( 'path' );
        document.getElementById("operationUserName").placeholder = $.i18n( 'operation_username' );
        document.getElementById("operationPassword").placeholder = $.i18n( 'operation_password' );
        document.getElementById("paramName").placeholder = $.i18n( 'param_name' );
        document.getElementById("paramValue").placeholder = $.i18n( 'val_optional' );
        document.getElementById("fieldName").placeholder = $.i18n( 'placeholder_val_optional' );
        document.getElementById("wsdlAddress").placeholder = $.i18n( 'wsdl_address' );
        document.getElementById("sapRouter").placeholder = $.i18n( 'sap_router_opt' );
        document.getElementById("sapLimit").placeholder = $.i18n( 'limit_opt' );
        document.getElementById("sapCapacity").placeholder = $.i18n( 'capacity_opt' );
        document.getElementById("sapExpirationTime").placeholder = $.i18n( 'expiration_time_opt' );
        document.getElementById("sapLanguage").placeholder = $.i18n( 'language_opt' );
        document.getElementById("sapPassword").placeholder = $.i18n( 'pass' );
        document.getElementById("sapUserName").placeholder = $.i18n( 'username' );
        document.getElementById("sapClient").placeholder = $.i18n( 'client' );
        document.getElementById("sapSysnr").placeholder = $.i18n( 'system_number' );
        document.getElementById("sapHost").placeholder = $.i18n( 'host' );
        document.getElementById("sapServiceName").placeholder = $.i18n( 'service_name' );
        document.getElementById("dnKey").placeholder = $.i18n( 'dnKey' );
        document.getElementById("serverAddress").placeholder = $.i18n( 'server_address' );
        
        $.loadScript('../plugins/data-tables/jquery.dataTables.js', function(){
            console.log('plugins/data-tables/jquery.dataTables.js');
        });
        $.loadScript('../plugins/data-tables/DT_bootstrap.js', function(){
            console.log('plugins/data-tables/DT_bootstrap.js');
        });
        
        $('#preview_link').prop('title', $.i18n( 'preview_app' ));
        $('#deployment_link').prop('title', $.i18n( 'deploy_app' ));
        $('#downloadApplication_link').prop('title', $.i18n( 'download_app' ));
        $('#deleteApplication_link').prop('title', $.i18n( 'delete_app' ));
        $('#applicationSettings_link').prop('title', $.i18n( 'app_prop' ));
        $('.fa-plus').prop('title', $.i18n( 'create_new_app' ));
        
        $('#addHeader').val($.i18n( 'add' ));
        $('#addOperation').val($.i18n( 'add' ));
        $('#addParams').val($.i18n( 'add' ));
        
} );