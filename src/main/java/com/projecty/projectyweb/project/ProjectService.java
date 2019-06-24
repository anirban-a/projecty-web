package com.projecty.projectyweb.project;

import com.projecty.projectyweb.misc.RedirectMessage;
import com.projecty.projectyweb.role.Role;
import com.projecty.projectyweb.role.RoleRepository;
import com.projecty.projectyweb.role.RoleService;
import com.projecty.projectyweb.role.Roles;
import com.projecty.projectyweb.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public ProjectService(ProjectRepository projectRepository, UserService userService, RoleRepository roleRepository, RoleService roleService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    public void save(Project project) {
        projectRepository.save(project);
    }

    private String checkCurrentUserAccessLevel(Project project) {
        Optional<Role> currentUserRole = roleRepository.findRoleByUserAndProject(userService.getCurrentUser(), project);
        return currentUserRole.map(Role::getName).orElse(null);
    }

    public boolean isCurrentUserProjectAdmin(Project project) {
        return checkCurrentUserAccessLevel(project).equals(Roles.ADMIN.toString());
    }

    public boolean isCurrentUserProjectUser(Project project) {
        String accessLevel = checkCurrentUserAccessLevel(project);
        return accessLevel.equals(Roles.ADMIN.toString()) || accessLevel.equals(Roles.USER.toString());
    }

    void createNewProjectAndSave(Project project, List<String> usernames, List<RedirectMessage> messages) {
        roleService.addCurrentUserToProjectAsAdmin(project);
        roleService.addRolesToProjectByUsernames(project, usernames, messages);
        projectRepository.save(project);
    }
}
