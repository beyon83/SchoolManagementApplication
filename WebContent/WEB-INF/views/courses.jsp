<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>   
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

<!-- For enabling Bootstrap's dropdown menu -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

<title>Subjects</title>
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
	        		<sec:authorize access="hasAuthority('Admin')">
						<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Register <span class="caret"></span></a>
	          				<ul class="dropdown-menu" role="menu">
	            				<li><a href="<c:url value="/register-admin" />">Admin</a></li>
	            				<li><a href="<c:url value="/register-teacher" />">Teacher</a></li>
	            				<li><a href="<c:url value="/register-student" />">Student</a></li>
	            				<li><a href="<c:url value="/add-course" />">New subject</a></li>
	          				</ul>
	        			</li>
	        			<li><a href="<c:url value="/admin-get-all-students" />">Students</a></li>
	        		</sec:authorize>
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
		<table class="table table-striped">
			<thead>
				<tr>
					<th>ID</th>
					<th>Subject title</th>
					<th>Teacher</th>
				</tr>
			</thead>
				<c:forEach items="${subjects}" var="subject">
					<tr>
						<td><c:out value="${subject.subjectId}" /></td>
						<td><c:out value="${subject.subjectTitle}" /></td>
						<c:choose>
							<c:when test="${subject.teacher.firstName == null}">
								<td style="color: grey;">
									<em><small><c:out value="Not assigned" /></small></em>
									<sec:authorize access="hasAuthority('Admin')">
										<small>(<a href="<c:url value='/edit-subject?id=${subject.subjectId}' />">Assign teacher</a>)</small>
									</sec:authorize>
								</td>
							</c:when>
						<c:otherwise>
							<td><c:out value="${subject.teacher.firstName} ${subject.teacher.lastName}" /></td>
						</c:otherwise>
						</c:choose>
					</tr>
				</c:forEach>
			<tbody>
			</tbody>
		</table>
    </div>

</body>
</html>