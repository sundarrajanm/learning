'''
Created on 13-Jul-2016

@author: sumuthur
'''
import unittest
from bowling.game import BowlingGame

class BowlingTest(unittest.TestCase):
    
    bowling_game = BowlingGame()
    def setup(self):
        self.bowling_game = BowlingGame()
    

    def rollPinsMany(self, pins, howMany):
        for i in range(1, howMany + 1):
            self.bowling_game.roll(pins)

    def testScore0_WhenAllRollsAreMissed(self):
        self.rollPinsMany(0, 20)        
        assert self.bowling_game.score() == 0

    def testScore20_OnePinEachBall(self):
        self.rollPinsMany(1, 20)
        print(self.bowling_game.score()) 
        assert self.bowling_game.score() == 20
