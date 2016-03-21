<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/styles.css" />" />
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<link href='http://fonts.googleapis.com/css?family=Oswald:300&subset=latin-ext' rel='stylesheet' type='text/css'>
<link href='https://fonts.googleapis.com/css?family=Slabo+27px' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Open+Sans+Condensed:300&subset=latin,latin-ext' rel='stylesheet' type='text/css'>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><c:out value="${student.firstName}" /></title>
</head>
<body>

	<!-- Bootstrap navigation bar  -->
	<nav class="navbar navbar-inverse" style="border-radius: 0px;">
		<div class="container-fluid">
			<div style="float: left; position: relative; top: 10px; right: 5px;">
				<img src="<c:url value="/resources/images/hat.png" />" />
			</div>
	    	<div class="navbar-header">
	      		<a class="navbar-brand" style="color: #e3e3e3;" href="<c:url value="/" />">School Management App</a>
	    	</div>
	    	<div>
	        	<ul class="nav navbar-nav">
	        		<!-- inside li: class="active" -->
	        		<li><a href="<c:url value="/courses" />">Subjects</a></li>
	        		<sec:authorize access="hasAuthority('Teacher')">
	        			<li><a href="<c:url value="/get-students" />">Students</a></li>
	        		</sec:authorize>
	        		<sec:authorize access="hasAuthority('Student')">
	        			<li><a href="<c:url value="/student-account?id=${loggedUser.id}" />">Account details</a></li>
	        		</sec:authorize>
	        		<c:if test="${loggedUser == null}">
	        			<li><a href="<c:url value="/login" />">Log in</a></li>
	        		</c:if>
	        		<c:if test="${loggedUser != null}">
	        			<li><a href="<c:url value="/logout" />">Log Out</a></li>
	        		</c:if>
<!-- 	        		<li style="width: 100px;"> -->
<!-- 						Bootstrap Search form -->
<!-- 						<form action="Search" method="get"> -->
<!-- 							<div class="row-search" style="margin-left: 300px; margin-top: 7px;"> -->
<!-- 						    	<div class="col-lg-6"> -->
<!-- 						        	<div class="input-group" style="width: 300px;"> -->
<!-- 						      			<input style="background-color: #444444;" type="text" name="query" class="form-control" placeholder="Search for..."> -->
<!-- 						      			<span class="input-group-btn"> -->
<%-- 						        			<a href="<c:url value="/Search?query=${query}" />"><button name="query" class="btn btn-default" type="button"><img src="images/search-icon.png" /></button></a> --%>
<!-- 						      			</span> -->
<!-- 						    		</div>/input-group -->
<!-- 						   		</div>/.col-lg-6 -->
<!-- 						    </div>/.row -->
<!-- 					    </form> -->
<!-- 				    </li> -->
	      		</ul>
	      		<ul class="nav" style="float: right;">
					<c:if test="${loggedUser != null}">
						<li><a id="logged-user" href="#"><img id="img-icon" src="<c:url value="/resources/images/user_avatar.png" />" /><c:out value="${loggedUser.username}" /></a></li>
					</c:if>
	      		</ul>
	    	</div>
		</div>
	</nav> <!-- End of navigation bar -->
	
    <div class="container">
    	<div class="user-avatar" style="float: left; padding-bottom: 10px;">
			<img src="<c:url value='/image/${student.id}' />" style="height: 100px; border: 6px solid #f2f2f2;" />
		</div>
    	<div class="student-header" style="margin-top: 10px;">
			<h4><span style="color: #2277aa; margin-left: 20px;"><c:out value="${student.firstName} ${student.lastName} (${student.username})" /></span></h4>
		</div>
    	<table class="table table-bordered" id="grades-cell">
    		<thead>
    			<tr id="grades-cell-row-header">
    				<th>Subjects:</th>
    				<th>Absences:</th>
    				<th>Grades:</th>
    				<th title="Grade point average">GPA:</th>
    			</tr>
    		</thead>
    		<tbody>
    			<c:forEach items="${student.subjects}" var="subject"> 
		    			<tr>
		    				<td><c:out value="${subject.subjectTitle}" /></td>
		    				<td><c:out value="N/A" /></td>
		    				<td>
		    					<c:set value="${0}" var="sum" />
		    					<c:set value="${0}" var="counter" />
		    					<c:forEach items="${student.grades}" var="grade">
									<c:if test="${subject.subjectTitle eq grade.subject.subjectTitle}">
										<c:if test="${grade.grade == 5 or grade.grade == 4}">
											<span style="color: green;"><c:out value="${grade.grade}" /></span> &nbsp;
										</c:if>
										<c:if test="${grade.grade == 2 or grade.grade == 3}">
											<span style="color: orange;"><c:out value="${grade.grade}" /></span> &nbsp;
										</c:if>
										<c:if test="${grade.grade == 1}">
											<span style="color: red;"><c:out value="${grade.grade}" /></span> &nbsp;
										</c:if>
										<c:set var="sum" value="${sum = sum + grade.grade}" />
										<c:set var="counter" value="${counter = counter + 1}" />
									</c:if>
		    					</c:forEach>
	    					</td>
		    				<td><fmt:formatNumber type="number" pattern="#.##" value="${sum / counter}" /></td>
		    			</tr>
    			</c:forEach>
    			<tr id="grades-cell-row-footer">
    				<td style="font-weight: bold;">Total:</td>
    				<td style="font-weight: bold;"><c:out value="${numbOfAbsences}" /></td>
    				<td style="font-weight: bold;">
<%--     					<c:forEach items="${totalGrades}" var="grade"> --%>
<%--     						<c:out value="${grade}" /> --%>
<%--     					</c:forEach> --%>
						<c:out value="N/A" />
    				</td>	
    				<td style="font-weight: bold;"><c:out value="${totalAverage}" /></td>
    			</tr>
    		</tbody>
    	</table>
    </div>
    
</body>
</html>