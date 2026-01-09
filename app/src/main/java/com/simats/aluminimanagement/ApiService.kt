package com.simats.aluminimanagement

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // --- Authentication ---
    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("forgot_password.php")
    fun forgotPassword(
        @Field("email") email: String
    ): Call<ForgotPasswordResponse>

    @FormUrlEncoded
    @POST("reset_password_direct.php")
    fun resetPasswordDirect(
        @Field("email") email: String,
        @Field("new_password") newPassword: String,
        @Field("user_type") userType: String
    ): Call<LoginResponse>

    // --- Dashboard & Admin Stats ---
    @GET("getalumnicount.php")
    fun getAlumniCount(): Call<CountResponse>

    @GET("getstudentcount.php")
    fun getStudentCount(): Call<CountResponse>

    @GET("getactiveeventscount.php")
    fun getActiveEventsCount(): Call<CountResponse>

    @GET("getjobpostscount.php")
    fun getJobPostsCount(): Call<CountResponse>

    @GET("getrecentactivity.php")
    fun getRecentActivity(): Call<List<RecentActivity>>

    @FormUrlEncoded
    @POST("get_mentee_count.php")
    fun getMenteeCount(@Field("email") email: String): Call<CountResponse>

    @FormUrlEncoded
    @POST("get_job_referrals_count.php")
    fun getJobReferralsCount(@Field("email") email: String): Call<CountResponse>

    // --- Events ---
    @GET("viewevents.php")
    fun getEvents(): Call<EventListResponse>

    @FormUrlEncoded
    @POST("addevent.php")
    fun addEvent(
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("event_date") date: String,
        @Field("event_time") time: String,
        @Field("venue") venue: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("updateevent.php")
    fun updateEvent(
        @Field("id") eventId: String,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("event_date") date: String,
        @Field("event_time") time: String,
        @Field("venue") venue: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("deleteevent.php")
    fun deleteEvent(
        @Field("id") eventId: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("registerevent.php")
    fun registerForEvent(
        @Field("roll_no") rollNo: String,
        @Field("event_id") eventId: Int
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("myevents.php")
    fun getMyRegisteredEvents(
        @Field("roll_no") rollNo: String
    ): Call<MyEventsResponse>

    // --- Mentorship ---
    @GET("viewmentor.php")
    fun getMentorList(): Call<MentorListResponse>

    @GET("get_available_mentors.php")
    fun getAvailableMentors(): Call<MentorListResponse>

    @FormUrlEncoded
    @POST("updatementorstatus.php")
    fun updateMentorStatus(
        @Field("roll_no") rollNo: String,
        @Field("status") status: String
    ): Call<UpdateStatusResponse>

    @FormUrlEncoded
    @POST("alumnimentorrequest.php")
    fun applyForMentorship(
        @Field("roll_no") rollNo: String,
        @Field("mentorship_field") field: String,
        @Field("working_hours") hours: String,
        @Field("mentorship_style") style: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("get_my_mentor_status.php")
    fun getMyMentorStatus(
        @Field("roll_no") rollNo: String
    ): Call<MyMentorStatusResponse>

    @FormUrlEncoded
    @POST("get_mentees.php")
    fun getMentees(@Field("mentor_roll_no") mentorRollNo: String): Call<MenteeListResponse>

    @FormUrlEncoded
    @POST("update_mentee_status.php")
    fun updateMenteeStatus(
        @Field("mentee_id") menteeId: String,
        @Field("status") status: String
    ): Call<UpdateMenteeStatusResponse>

    @FormUrlEncoded
    @POST("request_mentorship.php")
    fun requestMentorship(
        @Field("student_roll_no") studentRollNo: String,
        @Field("mentor_roll_no") mentorRollNo: String,
        @Field("topic") topic: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("get_my_mentor_requests.php")
    fun getMyMentorRequests(@Field("roll_no") studentRollNo: String): Call<MyMentorRequestsResponse>

    // --- Announcements ---
    @GET("viewannouncement.php")
    fun getAnnouncements(@Query("usertype") usertype: String?): Call<AnnouncementListResponse>

    @FormUrlEncoded
    @POST("addannouncement.php")
    fun addAnnouncement(
        @Field("title") title: String,
        @Field("message") message: String,
        @Field("target") target: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("deleteannouncement.php")
    fun deleteAnnouncement(
        @Field("id") id: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("updateannouncement.php")
    fun updateAnnouncement(
        @Field("id") id: String,
        @Field("title") title: String,
        @Field("message") message: String,
        @Field("target") target: String
    ): Call<LoginResponse>

    // --- Job Postings ---
    @GET("viewjobs.php")
    fun getJobs(): Call<JobListResponse>
    
    @FormUrlEncoded
    @POST("addjobs.php")
    fun addJob(
        @Field("title") title: String,
        @Field("company") company: String,
        @Field("description") description: String?,
        @Field("location") location: String?,
        @Field("job_type") jobType: String,
        @Field("salary") salary: String,
        @Field("last_date") lastDate: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("post_job_referral.php")
    fun postJobReferral(
        @Field("roll_no") rollNo: String,
        @Field("title") title: String,
        @Field("company") company: String,
        @Field("description") description: String,
        @Field("location") location: String?,
        @Field("job_type") jobType: String,
        @Field("salary") salary: String?,
        @Field("last_date") lastDate: String?
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("submit_job_application.php")
    fun submitJobApplication(
        @Field("roll_no") rollNo: String,
        @Field("job_id") jobId: String,
        @Field("full_name") fullName: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("skills") skills: String,
        @Field("experience") experience: String,
        @Field("current_company") currentCompany: String,
        @Field("linkedin_profile") linkedIn: String,
        @Field("cover_letter") coverLetter: String,
        @Field("expected_salary") expectedSalary: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("deletejobs.php")
    fun deleteJob(
        @Field("id") id: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("updatejobstatus.php")
    fun updateJobStatus(
        @Field("id") id: String,
        @Field("status") status: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("updatejobs.php")
    fun updateJob(
        @Field("id") id: String,
        @Field("title") title: String,
        @Field("company") company: String,
        @Field("description") description: String?,
        @Field("location") location: String?,
        @Field("job_type") jobType: String,
        @Field("salary") salary: String,
        @Field("last_date") lastDate: String
    ): Call<LoginResponse>

    // --- Fund Campaigns ---
    @GET("viewfund.php")
    fun getFunds(): Call<FundListResponse>

    @FormUrlEncoded
    @POST("addfund.php")
    fun addFund(
        @Field("fund_title") title: String,
        @Field("description") description: String?,
        @Field("target_amount") targetAmount: Double,
        @Field("last_date") lastDate: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("deletefund.php")
    fun deleteFund(
        @Field("id") id: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("updatefund.php")
    fun updateFund(
        @Field("id") id: String,
        @Field("fund_title") title: String,
        @Field("description") description: String?,
        @Field("target_amount") targetAmount: Double,
        @Field("collected_amount") collectedAmount: Double,
        @Field("last_date") lastDate: String
    ): Call<LoginResponse>

    // --- Registration & Profile ---
    @FormUrlEncoded
    @POST("student_register.php")
    fun registerStudent(
        @Field("roll_no") rollNo: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("year") year: String,
        @Field("department") department: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("alumni_register.php")
    fun registerAlumni(
        @Field("roll_no") rollNo: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("graduation_year") graduationYear: String,
        @Field("degree") degree: String,
        @Field("department") department: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("admin_register.php")
    fun registerAdmin(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Call<LoginResponse>
    
    @FormUrlEncoded
    @POST("get_alumni_profile.php")
    fun getAlumniProfile(
        @Field("roll_no") rollNo: String
    ): Call<AlumniProfileResponse>

    @FormUrlEncoded
    @POST("update_alumni_profile.php")
    fun updateAlumniProfile(
        @Field("roll_no") rollNo: String,
        @Field("name") name: String,
        @Field("company") company: String?,
        @Field("location") location: String?,
        @Field("department") department: String?,
        @Field("batch_year") batchYear: String?
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("update_student_profile.php")
    fun updateStudentProfile(
        @Field("roll_no") rollNo: String,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("department") department: String,
        @Field("address") address: String,
        @Field("cgpa") cgpa: String,
        @Field("interests") interests: String
    ): Call<LoginResponse>

    // --- Directory & Community ---
    @GET("view_alumni_directory.php")
    fun getAlumniDirectory(): Call<AlumniDirectoryResponse>

    @GET("get_alumni.php")
    fun getAlumni(): Call<AlumniListResponse>

    @GET("getstudents.php")
    fun getStudents(): Call<StudentListResponse>

    @FormUrlEncoded
    @POST("get_communities.php")
    fun getCommunities(@Field("roll_no") rollNo: String): Call<CommunityListResponse>

    @FormUrlEncoded
    @POST("join_community.php")
    fun joinCommunity(
        @Field("roll_no") rollNo: String,
        @Field("community_id") communityId: Int
    ): Call<JoinCommunityResponse>

    @FormUrlEncoded
    @POST("create_community.php")
    fun createCommunity(
        @Field("roll_no") creatorRollNo: String,
        @Field("name") name: String,
        @Field("description") description: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("get_community_posts.php")
    fun getCommunityPosts(
        @Field("community_id") communityId: Int,
        @Field("roll_no") rollNo: String
    ): Call<CommunityPostsResponse>

    @FormUrlEncoded
    @POST("create_community_post.php")
    fun createCommunityPost(
        @Field("community_id") communityId: Int,
        @Field("roll_no") rollNo: String,
        @Field("content") content: String
    ): Call<CreatePostResponse>

    @FormUrlEncoded
    @POST("like_community_post.php")
    fun likePost(
        @Field("post_id") postId: Int,
        @Field("roll_no") rollNo: String
    ): Call<LoginResponse>
}