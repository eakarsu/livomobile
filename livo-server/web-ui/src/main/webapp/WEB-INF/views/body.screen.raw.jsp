<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>


<c:choose>
	<c:when test="${what==true}">

		<div id="typeHolder" style="display: none;" data-type="${type}"></div>
		<form id="rawScreenEdit" method="post"
			class="form-horizontal row-border" action="">
			<input type="hidden" name="applicationId"
				value="${application.name}"> <br>
			<div class="form-group">

				<div class="col-sm-12">
					<textarea id="rawScreenEditor" style="width: 99%;"
						class="form-control" name="editor" rows="24"><c:out
							value="${content}" /></textarea>
				</div>
			</div>
			<span data-i18n="editor_command_info"></span><span data-i18n="editor_command_f11"></span><code>F11</code>/<code>Shift-Esc</code>, <span data-i18n="editor_command_esc"></span><code>ESC</code>, <span data-i18n="editor_command_ctrls"></span><code>CTRL-S</code>, <span data-i18n="editor_command_ctrld"></span><code>CTRL-D</code>, <span data-i18n="editor_command_ctrlspace"></span><code>CTRL-SPACE</code>. <span data-i18n="editor_command_info_link"></span><a href=\"javascript:showShortcuts();\" id=\"idShortcuts\" data-i18n="editor_command_info_link_text"></a>.

			<div class="bottom " style="background: none; border: none;">
				<button class="btn btn-primary"
					style="float: right; margin-right: 5px;" type="button"
					value="editorSubmitAndDeploy"
					onclick="saveAndDeploy();
                            return false;"
					id="editorSubmitAndDeploy" data-i18n="save_deploy"></button>
				<button class="btn btn-primary"
					style="float: right; margin-right: 5px;" type="submit"
					value="editorSubmit" id="editorSubmit" data-i18n="save"></button>
			</div>
		</form>
	</c:when>
	<c:otherwise>
		<img src="data:image/jpeg;base64,${content}" alt=""
			style="max-width: 99%; margin: auto;">
	</c:otherwise>
</c:choose>

<script type="text/javascript">

    $('body').i18n();

</script>
