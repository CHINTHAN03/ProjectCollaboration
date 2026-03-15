import io.javalin.Javalin;
import dao.*;
import models.*;
import org.bson.Document;
import utils.DatabaseConnection;

public class App {
    public static void main(String[] args) {
        DatabaseConnection.getDatabase();

        UserDAO userDAO = new UserDAO();
        ProjectDAO projectDAO = new ProjectDAO();
        TaskDAO taskDAO = new TaskDAO();
        AnalyticsDAO analyticsDAO = new AnalyticsDAO();
        CommentDAO commentDAO = new CommentDAO();
        AdminDAO adminDAO = new AdminDAO(); // NEW

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> cors.addRule(it -> it.anyHost()));
        }).start(7070);

        // --- USERS ---
        app.post("/api/register", ctx -> {
            User newUser = ctx.bodyAsClass(User.class);
            if (userDAO.registerUser(newUser)) ctx.status(201).json("{\"message\": \"Success\"}");
            else ctx.status(400).json("{\"error\": \"Username taken\"}");
        });

        app.post("/api/login", ctx -> {
            User creds = ctx.bodyAsClass(User.class);
            User user = userDAO.loginUser(creds.getUsername(), creds.getPassword());
            if (user != null) ctx.status(200).json("{\"message\": \"Success\", \"role\": \"" + user.getRole() + "\"}");
            else ctx.status(401).json("{\"error\": \"Invalid credentials\"}");
        });

        // --- PROJECTS ---
        app.post("/api/projects", ctx -> {
            Project p = ctx.bodyAsClass(Project.class);
            if (projectDAO.createProject(p)) ctx.status(201).json("{\"message\": \"Success\"}");
            else ctx.status(500).json("{\"error\": \"Failed\"}");
        });

        app.get("/api/projects", ctx -> ctx.status(200).json(projectDAO.getAllProjects()));

        // --- TASKS ---
        app.post("/api/tasks", ctx -> {
            Task t = ctx.bodyAsClass(Task.class);
            if (taskDAO.createTask(t)) ctx.status(201).json("{\"message\": \"Success\"}");
            else ctx.status(500).json("{\"error\": \"Failed\"}");
        });

        app.get("/api/projects/{projectId}/tasks", ctx -> {
            ctx.status(200).json(taskDAO.getTasksByProjectId(ctx.pathParam("projectId")));
        });

        app.put("/api/tasks/{taskId}/status", ctx -> {
            String newStatus = Document.parse(ctx.body()).getString("status");
            if (taskDAO.updateTaskStatus(ctx.pathParam("taskId"), newStatus)) ctx.status(200).json("{\"message\": \"Success\"}");
            else ctx.status(500).json("{\"error\": \"Failed\"}");
        });

        // --- INTELLIGENCE ENGINE ---
        app.get("/api/projects/{projectId}/recommend", ctx -> {
            String recommendedAssignee = taskDAO.recommendAssignee(ctx.pathParam("projectId"));
            ctx.status(200).json("{\"recommendedUser\": \"" + recommendedAssignee + "\"}");
        });

        // --- COMMENTS ---
        app.post("/api/tasks/{taskId}/comments", ctx -> {
            Comment c = ctx.bodyAsClass(Comment.class);
            c.setTaskId(ctx.pathParam("taskId"));
            if (commentDAO.addComment(c)) ctx.status(201).json("{\"message\": \"Success\"}");
            else ctx.status(500).json("{\"error\": \"Failed\"}");
        });

        app.get("/api/tasks/{taskId}/comments", ctx -> {
            ctx.status(200).json(commentDAO.getCommentsByTaskId(ctx.pathParam("taskId")));
        });

        // --- ANALYTICS ---
        app.get("/api/projects/{projectId}/analytics", ctx -> {
            ctx.status(200).json(analyticsDAO.getProjectMetrics(ctx.pathParam("projectId")));
        });

        // --- ADMIN SECURE ROUTES (NEW) ---
        app.delete("/api/admin/wipe", ctx -> {
            if (adminDAO.wipeWorkspace()) ctx.status(200).json("{\"message\": \"Workspace heavily purged.\"}");
            else ctx.status(500).json("{\"error\": \"Wipe failed.\"}");
        });

        app.get("/api/admin/users", ctx -> {
            ctx.status(200).json(adminDAO.getAllUsers());
        });

        app.delete("/api/admin/users/{username}", ctx -> {
            if (adminDAO.removeUser(ctx.pathParam("username"))) ctx.status(200).json("{\"message\": \"User removed.\"}");
            else ctx.status(500).json("{\"error\": \"Failed to remove user.\"}");
        });

        System.out.println("Enterprise Engine initialized. Awaiting requests...");
    }
}