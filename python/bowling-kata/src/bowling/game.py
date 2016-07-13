class BowlingGame(object):
    
    points = []
    
    def roll(self, pins):
        self.points.append(pins)

    
    def score(self):
        total = 0
        for pt in self.points:
            total += pt
        return total   
    
    
    
    



