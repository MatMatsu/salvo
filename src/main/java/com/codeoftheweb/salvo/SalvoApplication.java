package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SalvoApplication.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(GameRepository gameRepository, PlayerRepository playersRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ShipTypeRepository shipTypeRepository, ScoreRepository scoreRepository) {
        return (args) -> {
            // CREATE PASSWORD
            PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            String pass0 = passwordEncoder.encode("admin");
            String pass1 = passwordEncoder.encode("24");
            String pass2 = passwordEncoder.encode("42");
            String pass3 = passwordEncoder.encode("kb");
            String pass4 = passwordEncoder.encode("mole");

            // CREATE PLAYERS
            Player player0 = new Player("admin@admin", pass0);
            Player player1 = new Player("j.bauer@ctu.gov", pass1);
            Player player2 = new Player("c.obrian@ctu.gov", pass2);
            Player player3 = new Player("kim_bauer@gmail.com", pass3);
            Player player4 = new Player("t.almeida@ctu.gov", pass4);


            // SAVE PLAYERS
            playersRepository.save(player0);
            playersRepository.save(player1);
            playersRepository.save(player2);
            playersRepository.save(player3);
            playersRepository.save(player4);

            // CREATE GAMES
            Game game1 = new Game(1);
            Game game2 = new Game(2);
            Game game3 = new Game(3);
            Game game4 = new Game(4);
            Game game5 = new Game(5);
            Game game6 = new Game(6);
            Game game7 = new Game(7);
            Game game8 = new Game(8);

            // SAVE GAMES
            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);
            gameRepository.save(game4);
            gameRepository.save(game5);
            gameRepository.save(game6);
            gameRepository.save(game7);
            gameRepository.save(game8);


            // CREATE GAMEPLAYERS
            // GAME 1
            GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
            GamePlayer gamePlayer2 = new GamePlayer(game1, player2);
            // GAME 2
            GamePlayer gamePlayer3 = new GamePlayer(game2, player1);
            GamePlayer gamePlayer4 = new GamePlayer(game2, player2);
            // GAME 3
            GamePlayer gamePlayer5 = new GamePlayer(game3, player2);
            GamePlayer gamePlayer6 = new GamePlayer(game3, player4);
            // GAME 4
            GamePlayer gamePlayer7 = new GamePlayer(game4, player2);
            GamePlayer gamePlayer8 = new GamePlayer(game4, player1);
            // GAME 5
            GamePlayer gamePlayer9 = new GamePlayer(game5, player4);
            GamePlayer gamePlayer10 = new GamePlayer(game5, player1);
            // GAME 6
            GamePlayer gamePlayer11 = new GamePlayer(game6, player3);
            // GAME 7
            GamePlayer gamePlayer12 = new GamePlayer(game7, player4);
            // GAME 8
            GamePlayer gamePlayer13 = new GamePlayer(game8, player3);
            GamePlayer gamePlayer14 = new GamePlayer(game8, player4);


            // SAVE GAMES AND PLAYERS IN GAMEPLAYERS
            gamePlayerRepository.save(gamePlayer1);
            gamePlayerRepository.save(gamePlayer2);
            gamePlayerRepository.save(gamePlayer3);
            gamePlayerRepository.save(gamePlayer4);
            gamePlayerRepository.save(gamePlayer5);
            gamePlayerRepository.save(gamePlayer6);
            gamePlayerRepository.save(gamePlayer7);
            gamePlayerRepository.save(gamePlayer8);
            gamePlayerRepository.save(gamePlayer9);
            gamePlayerRepository.save(gamePlayer10);
            gamePlayerRepository.save(gamePlayer11);
            gamePlayerRepository.save(gamePlayer12);
            gamePlayerRepository.save(gamePlayer13);
            gamePlayerRepository.save(gamePlayer14);


            // CREATE SHIPS TYPE
            ShipType carrier = new ShipType("carrier", 1, 5);
            ShipType battleship = new ShipType("battleship", 1, 4);
            ShipType destroyer = new ShipType("destroyer", 1, 3);
            ShipType submarine = new ShipType("submarine", 1, 3);
            ShipType patrolBoat = new ShipType("patrolBoat", 1, 2);


            //SAVE SHIPS TYPE
            shipTypeRepository.save(carrier);
            shipTypeRepository.save(battleship);
            shipTypeRepository.save(destroyer);
            shipTypeRepository.save(submarine);
            shipTypeRepository.save(patrolBoat);


            //CREATE SHIPS AND LOCATIONS
            // GAME 1
            Ship ship1 = new Ship(destroyer, Arrays.asList("H2", "H3", "H4"), gamePlayer1);
            Ship ship2 = new Ship(submarine, Arrays.asList("E1", "F1", "G1"), gamePlayer1);
            Ship ship3 = new Ship(patrolBoat, Arrays.asList("B4", "B5"), gamePlayer1);
            Ship ship4 = new Ship(destroyer, Arrays.asList("B5", "C5", "D5"), gamePlayer2);
            Ship ship5 = new Ship(patrolBoat, Arrays.asList("F1", "F2"), gamePlayer2);
            // GAME 2
            Ship ship6 = new Ship(destroyer, Arrays.asList("B5", "C5", "D5"), gamePlayer3);
            Ship ship7 = new Ship(patrolBoat, Arrays.asList("C6", "C7"), gamePlayer3);
            Ship ship8 = new Ship(submarine, Arrays.asList("A2", "A3", "A4"), gamePlayer4);
            Ship ship9 = new Ship(patrolBoat, Arrays.asList("G6", "H6"), gamePlayer4);
            // GAME 3
            Ship ship10 = new Ship(destroyer, Arrays.asList("B5", "C5", "D5"), gamePlayer5);
            Ship ship11 = new Ship(patrolBoat, Arrays.asList("C6", "C7"), gamePlayer5);
            Ship ship12 = new Ship(submarine, Arrays.asList("A2", "A3", "A4"), gamePlayer6);
            Ship ship13 = new Ship(patrolBoat, Arrays.asList("G6", "H6"), gamePlayer6);
            // GAME 4
            Ship ship14 = new Ship(destroyer, Arrays.asList("B5", "C5", "D5"), gamePlayer7);
            Ship ship15 = new Ship(patrolBoat, Arrays.asList("C6", "C7"), gamePlayer7);
            Ship ship16 = new Ship(submarine, Arrays.asList("A2", "A3", "A4"), gamePlayer8);
            Ship ship17 = new Ship(patrolBoat, Arrays.asList("G6", "H6"), gamePlayer8);
            // GAME 5
            Ship ship18 = new Ship(destroyer, Arrays.asList("B5", "C5", "D5"), gamePlayer9);
            Ship ship19 = new Ship(patrolBoat, Arrays.asList("C6", "C7"), gamePlayer9);
            Ship ship20 = new Ship(submarine, Arrays.asList("A2", "A3", "A4"), gamePlayer10);
            Ship ship21 = new Ship(patrolBoat, Arrays.asList("G6", "H6"), gamePlayer10);
            // GAME 6
            Ship ship22 = new Ship(destroyer, Arrays.asList("B5", "C5", "D5"), gamePlayer11);
            Ship ship23 = new Ship(patrolBoat, Arrays.asList("C6", "C7"), gamePlayer11);
            // GAME 7

            // GAME 8
            Ship ship26 = new Ship(destroyer, Arrays.asList("B5", "C5", "D5"), gamePlayer13);
            Ship ship27 = new Ship(patrolBoat, Arrays.asList("C6", "C7"), gamePlayer13);
            Ship ship28 = new Ship(submarine, Arrays.asList("A2", "A3", "A4"), gamePlayer14);
            Ship ship29 = new Ship(patrolBoat, Arrays.asList("G6", "H6"), gamePlayer14);

            // SAVE SHIPS AND LOCATIONS
            shipRepository.save(ship1);
            shipRepository.save(ship2);
            shipRepository.save(ship3);
            shipRepository.save(ship4);
            shipRepository.save(ship5);
            shipRepository.save(ship6);
            shipRepository.save(ship7);
            shipRepository.save(ship8);
            shipRepository.save(ship9);
            shipRepository.save(ship10);
            shipRepository.save(ship11);
            shipRepository.save(ship12);
            shipRepository.save(ship13);
            shipRepository.save(ship14);
            shipRepository.save(ship15);
            shipRepository.save(ship16);
            shipRepository.save(ship17);
            shipRepository.save(ship18);
            shipRepository.save(ship19);
            shipRepository.save(ship20);
            shipRepository.save(ship21);
            shipRepository.save(ship22);
            shipRepository.save(ship23);
        /*shipRepository.save(ship24);
        shipRepository.save(ship25);*/
            shipRepository.save(ship26);
            shipRepository.save(ship27);
            shipRepository.save(ship28);
            shipRepository.save(ship29);

            // CREATE SALVOES
            // GAME 1
            Salvo salvo1 = new Salvo(1, gamePlayer1, Arrays.asList("B5", "C5", "F1"));
            Salvo salvo2 = new Salvo(1, gamePlayer2, Arrays.asList("B4", "B5", "B6"));
            Salvo salvo3 = new Salvo(2, gamePlayer1, Arrays.asList("F2", "D5"));
            Salvo salvo4 = new Salvo(2, gamePlayer2, Arrays.asList("E1", "H3", "A2"));
            // GAME 2
            Salvo salvo5 = new Salvo(1, gamePlayer3, Arrays.asList("A2", "A4", "G6"));
            Salvo salvo6 = new Salvo(1, gamePlayer4, Arrays.asList("B5", "D5", "C7"));
            Salvo salvo7 = new Salvo(2, gamePlayer3, Arrays.asList("A3", "H6"));
            Salvo salvo8 = new Salvo(2, gamePlayer4, Arrays.asList("C5", "C6"));
            // GAME 3
            Salvo salvo9 = new Salvo(1, gamePlayer5, Arrays.asList("G6", "H6", "A4"));
            Salvo salvo10 = new Salvo(1, gamePlayer6, Arrays.asList("H1", "H2", "H3"));
            Salvo salvo11 = new Salvo(2, gamePlayer5, Arrays.asList("A2", "A3", "D8"));
            Salvo salvo12 = new Salvo(2, gamePlayer6, Arrays.asList("E1", "F2", "G3"));
            // GAME 4
            Salvo salvo13 = new Salvo(1, gamePlayer7, Arrays.asList("A3", "A4", "F7"));
            Salvo salvo14 = new Salvo(1, gamePlayer8, Arrays.asList("B5", "C6", "H1"));
            Salvo salvo15 = new Salvo(2, gamePlayer7, Arrays.asList("A2", "G6", "H6"));
            Salvo salvo16 = new Salvo(2, gamePlayer8, Arrays.asList("C5", "C7", "D5"));
            // GAME 5
            Salvo salvo17 = new Salvo(1, gamePlayer9, Arrays.asList("A1", "A2", "A3"));
            Salvo salvo18 = new Salvo(1, gamePlayer10, Arrays.asList("B5", "B6", "C7"));
            Salvo salvo19 = new Salvo(2, gamePlayer9, Arrays.asList("G6", "G7", "G8"));
            Salvo salvo20 = new Salvo(2, gamePlayer10, Arrays.asList("C6", "D6", "E6"));
            Salvo salvo21 = new Salvo(3, gamePlayer10, Arrays.asList("H1", "H8"));

            // SAVE SALVOES
            salvoRepository.save(salvo1);
            salvoRepository.save(salvo2);
            salvoRepository.save(salvo3);
            salvoRepository.save(salvo4);
            salvoRepository.save(salvo5);
            salvoRepository.save(salvo6);
            salvoRepository.save(salvo7);
            salvoRepository.save(salvo8);
            salvoRepository.save(salvo9);
            salvoRepository.save(salvo10);
            salvoRepository.save(salvo11);
            salvoRepository.save(salvo12);
            salvoRepository.save(salvo13);
            salvoRepository.save(salvo14);
            salvoRepository.save(salvo15);
            salvoRepository.save(salvo16);
            salvoRepository.save(salvo17);
            salvoRepository.save(salvo18);
            salvoRepository.save(salvo19);
            salvoRepository.save(salvo20);
            salvoRepository.save(salvo21);


            // CREATE SCORES
            // SCORE 1
            Score score1 = new Score(game1, player1, 1f);
            Score score2 = new Score(game1, player2, 0f);
            // SCORE 2
            Score score3 = new Score(game2, player1, 0.5f);
            Score score4 = new Score(game2, player2, 0.5f);
            // SCORE 3
            Score score5 = new Score(game3, player2, 1f);
            Score score6 = new Score(game3, player4, 0f);
            // SCORE 4
            Score score7 = new Score(game4, player2, 0.5f);
            Score score8 = new Score(game4, player1, 0.5f);
            // SCORE 5
            Score score9 = new Score(game5, player4, -1f);
            Score score10 = new Score(game5, player1, -1f);
            // SCORE 6
            Score score11 = new Score(game6, player3, -1f);
            // SCORE 7
            Score score12 = new Score(game7, player4, -1f);
            // SCORE 8
            Score score13 = new Score(game8, player3, -1f);
            Score score14 = new Score(game8, player4, -1f);

            scoreRepository.save(score1);
            scoreRepository.save(score2);
            scoreRepository.save(score3);
            scoreRepository.save(score4);
            scoreRepository.save(score5);
            scoreRepository.save(score6);
            scoreRepository.save(score7);
            scoreRepository.save(score8);

        };

	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName-> {
            Player player = playerRepository.findByUserName(inputName);
            if (player != null && !player.getUserName().equals("admin@admin")) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else if (player != null && player.getUserName().equals("admin@admin")) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("ADMIN", "USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()// turn off checking for CSRF tokens
            .authorizeRequests()
                //++++++++++++++++++++++++++++++++++++++
                //REVISAR LAS AUTORIZACIONES LOGUEO
                //++++++++++++++++++++++++++++++++++++++
                // PERMITE INGRESO A TODO PARA TODOS
                /*
                .antMatchers("/").permitAll()
                .antMatchers("/console/**").permitAll()
                .and()
                .headers().frameOptions().disable()
                */

                .antMatchers("/web/games.html", "/api/games", "/api/game/**" , "/api/players", "/web/styles/style.css", "/web/scripts/games.js", "/web/scripts/login.js").permitAll()
                .antMatchers("/web/**", "/api/**", "/topic/**", "/turno/**").hasAuthority("USER").anyRequest().authenticated()
                .antMatchers("/h2-console/**", "/**").hasAuthority("ADMIN").anyRequest().authenticated()
                .and()
                .headers().frameOptions().disable()

                .and()
            .formLogin()
                .loginPage("/web/games.html").permitAll()
                .defaultSuccessUrl("/web/games.html", true)
                .failureUrl("/web/games.html?fail=true")
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/web/games.html?logout=true");
    }


    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}