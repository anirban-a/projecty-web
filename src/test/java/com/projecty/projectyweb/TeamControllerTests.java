package com.projecty.projectyweb;

import com.projecty.projectyweb.project.Project;
import com.projecty.projectyweb.team.Team;
import com.projecty.projectyweb.team.TeamRepository;
import com.projecty.projectyweb.team.role.TeamRole;
import com.projecty.projectyweb.team.role.TeamRoles;
import com.projecty.projectyweb.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProjectyWebApplication.class)
@AutoConfigureMockMvc
public class TeamControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamRepository teamRepository;

    private Team team;
    private Project project;

    @Before
    public void init() {
        User user = new User();
        team = new Team();
        user.setUsername("user");
        team.setId(1L);
        team.setName("team");

        TeamRole teamRole = new TeamRole();
        teamRole.setUser(user);
        teamRole.setName(TeamRoles.MANAGER);
        teamRole.setTeam(team);

        List<TeamRole> teamRoles = new ArrayList<>();
        team.setTeamRoles(teamRoles);
        user.setTeamRoles(teamRoles);
        project = new Project();
        project.setName("Sample");

        Mockito.when(teamRepository.findById(team.getId()))
                .thenReturn(java.util.Optional.ofNullable(team));

    }

    @Test
    @WithMockUser
    public void givenRequestOnMyTeams_shouldReturnMyTeamsView() throws Exception {
        mockMvc.perform(get("/team/myTeams"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("teamRoles"))
                .andExpect(view().name("fragments/team/my-teams"));
    }

    @Test
    @WithMockUser
    public void givenRequestOnAddTeam_shouldReturnMyTeamsView() throws Exception {
        mockMvc.perform(post("/team/addTeam")
                .flashAttr("team", team))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/team/myTeams"));
    }

    @Test
    @WithMockUser
    public void givenRequestOnAddProjectTeam_shouldRedirectToMyTeams() throws Exception {
        mockMvc.perform(post("/team/addProjectTeam")
                .flashAttr("project", project)
                .param("teamId", "1"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/team/myTeams"));
    }

}
