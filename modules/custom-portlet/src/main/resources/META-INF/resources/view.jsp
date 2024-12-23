<%@ include file="/init.jsp" %>

<p>
	<portlet:actionURL name="doSomething" var="actionURL" />
	<a href="<%= actionURL %>">Do Something</a>
</p>