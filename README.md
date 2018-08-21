# Battle bots

Set up arenas, connect bots and let them do battle

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

Then go to [localhost:3000](http://localhost:3000 ) to start

# Integration with bots

Before any games can be run, they need an arena. This can be done via the frontend.
Provide a name, the amount of rounds and the game. Different games have different rules,
but all expect that each contenstant provides an endpoint that can recieve POST requests
and which returns it's next move. Each round consists of the server sending the current
state to each contenstant, after which it collects all responses and updates the score.
Each contenstant will recieve a JSON object like the following:

    [
      {"id": "id1", "name": "contenstant 1", "score": 12, "move": "paper"},
      {"id": "id2", "name": "contenstant 2", "score": 2, "move": "paper"},
      {"id": "id3", "name": "contenstant 3", "score": 5, "move": "rock"},
    ]

Which means that in last round players 1 and 2 played "paper", while player 3 played "rock".
The previous round resulted in player 1 having 12 points, player 2 2 points and
player 3 5 points.

If a player returns an invalid value, that player will be skipped during that round

## Available games:

### Rock, paper, scissors

During each round, each player shows one of rock, paper or scissors. Then the winner
is last single player standing after the following eliminations:
* any player that plays paper loses if any other player played scissors
* any player that plays scissors loses if any other player played rock
* any player that plays rock loses if any other player played paper

The only valid moves are "rock", "paper" or "scissors" - any other values result in
a player automatically losing a given round.
For testing, there is and endpoint ([/random_rock](http://localhost:3000/random_rock)) which randomly returns one of the
above values.

## License

Copyright © 2018 FIXME
