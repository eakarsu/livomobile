		<!-- BEGIN SIDEBAR -->
		<div class="page-sidebar-wrapper">
			<!-- DOC: Set data-auto-scroll="false" to disable the sidebar from auto scrolling/focusing -->
			<!-- DOC: Change data-auto-speed="200" to adjust the sub menu slide up/down speed -->
			<div class="page-sidebar navbar-collapse collapse">
				<!-- BEGIN SIDEBAR MENU -->
				<!-- DOC: Apply "page-sidebar-menu-light" class right after "page-sidebar-menu" to enable light sidebar menu style(without borders) -->
				<!-- DOC: Apply "page-sidebar-menu-hover-submenu" class right after "page-sidebar-menu" to enable hoverable(hover vs accordion) sub menu mode -->
				<!-- DOC: Apply "page-sidebar-menu-closed" class right after "page-sidebar-menu" to collapse("page-sidebar-closed" class must be applied to the body element) the sidebar sub menu mode -->
				<!-- DOC: Set data-auto-scroll="false" to disable the sidebar from auto scrolling/focusing -->
				<!-- DOC: Set data-keep-expand="true" to keep the submenues expanded -->
				<!-- DOC: Set data-auto-speed="200" to adjust the sub menu slide up/down speed -->
				<ul class="page-sidebar-menu " data-keep-expanded="false"
					data-auto-scroll="true" data-slide-speed="200">
					<li><a href="/dashboard" id="dashboardPage"> <i
							class="icon-home"></i> <span class="title">Welcome</span>
					</a></li>
					<li><a href="/termsofuse?email=${loggedInUser.email}" id="termsofuse"> <i class="icon-present"></i> <span
							class="title">The Terms of Use</span>
					</a></li>
					<li><a href="/pricing"> <i class="icon-basket"></i> <span
							class="title">Pricing</span>
					</a></li>

				</ul>
				<!-- END SIDEBAR MENU -->
			</div>
		</div>
<!-- 			</div> -->
		<!-- END SIDEBAR -->