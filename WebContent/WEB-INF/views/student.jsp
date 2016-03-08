<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
	
	<!-- Columns are always 50% wide, on mobile and desktop -->
	<div class="container" style="width: 450px;">
		<div class="row">
			<div class="student-header">
				<h4>STUDENT: <span style="color: #2277aa; margin-left: 20px;"><c:out value="${student.firstName} ${student.lastName}" /></span></h4>
			</div>
  			<div class="student-row">
  				<label class="row-label">First name:</label>
  				<c:out value="${student.firstName}" />
  			</div>
  			<div class="student-row">
  				<label class="row-label">Last name:</label>
  				<c:out value="${student.lastName}" />
  			</div>
  			<div class="student-row">
  				<label class="row-label">Username:</label>
  				<c:out value="${student.username}" />
  			</div>
  			<div class="student-row">
  				<label class="row-label">Absences:</label>
  				<c:out value="${numbOfAbsences}" />
  			</div>
  			<div class="student-row">
  				<label class="row-label">GPA:</label>
  				<c:out value="${totalAverage}" />
  			</div>
  			<div class="student-row">
  				<label class="row-label">Subject:</label>
  				<c:out value="${subject.subjectTitle}" />
  			</div>
  			<div class="student-row">
  				<label class="row-label">Grades:</label>
  				<c:forEach items="${grades}" var="grade">
  					<c:out value="${grade.grade}" />
  				</c:forEach>
  			</div>
  			
  			<c:url value="/assign-grade?studentId=${student.id}&student=${student.username}&subjectId=${subject.subjectId}" var="gradeUrl" />
  			<form:form action="${gradeUrl}" modelAttribute="student">
	  			<div class="student-row">
	  				<label class="row-label">Assign grade:</label>
	  				1:<input type="radio" name="grade" value="1" />&nbsp;
	  				2:<input type="radio" name="grade" value="2" />&nbsp;
	  				3:<input type="radio" name="grade" value="3" />&nbsp;
	  				4:<input type="radio" name="grade" value="4" />&nbsp;
	  				5:<input type="radio" name="grade" value="5" />
	  				<input type="hidden" name="grade" value="0" checked />
	  			</div>
	  			<div class="student-row">
	  				<label class="row-label">Attending?</label>
	  				<input type="radio" name="attendingClass" value="true" checked />Yes &nbsp;
	  				<input type="radio" name="attendingClass" value="false" />No
	  			</div>
	  			<div class="form-group">
	  				<input type="submit" class="btn btn-info btn-block" value="Submit changes" />
	  			</div>
  			</form:form>
  			
		</div>
    </div>

</body>
</html>