# Manu Mäki-Rajala
# This code is an implementation of Q-learning, a reinforcement learning algorithm,
# applied to the OpenAI Gym Taxi-v3 environment.

# The project was implemented as part of the machine learning and 
# pattern recognition course.

# Load OpenAI Gym and other necessary packages
import gym
import random
import numpy as np
import time

# Environment
env = gym.make("Taxi-v3", render_mode='ansi')

# Training parameters for Q learning
alpha = 0.9 # Learning rate
gamma = 0.9 # Future reward discount factor
num_of_episodes = 1000
num_of_steps = 500 # per each episode

# Q table for rewards
#Q_reward = -100000*numpy.ones((500,6)) # All same
#Q_reward = -100000*numpy.random.random((500, 6)) # Random
num_of_states = env.observation_space.n
num_of_actions = env.action_space.n
# Empty Q table
Q_reward = np.zeros((num_of_states, num_of_actions))

# Training w/ random sampling actions
for episode in range(num_of_episodes):
    state = env.reset()
    state = state[0]
    total_reward = 0

    for step in range(num_of_steps):
        # Choose exploration or exploitation Epsilon = 0.5
        # I tried with 0.1, 0.2, 0.3, 0.4, 0.5, 0.6
        # and 0.5 gave the best rewards

        # Explotation
        action = np.argmax(Q_reward[state, :])

        # Exploration
        if random.uniform(0, 1) < 0.5:
            action = env.action_space.sample()

        next_state, reward, done, info, _ = env.step(action)
        # Q-learning update rule:
        Q_reward[state, action] = Q_reward[state,action]+alpha*(reward+gamma*np.max(Q_reward[next_state,:])-
                                                                Q_reward[state, action])
        state = next_state
        total_reward += reward

def test():
    state = env.reset()[0]
    tot_reward = 0
    num_of_actions = 0
    for t in range(50):
        action = np.argmax(Q_reward[state,:])
        num_of_actions += 1
        state, reward, done, truncated, info = env.step(action)
        tot_reward += reward
        print(env.render())
        time.sleep(1)
        if done:
            print("Total reward %d" %tot_reward)
            break

    return tot_reward, num_of_actions

# Testing 10 times and counting average reward and num of actions
reward_list = []
actions_count_list = []

for i in range(10):
    print("\nRun number ", i)
    total_reward, num_of_actions = test()
    reward_list.append(total_reward)
    actions_count_list.append(num_of_actions)

# Count and print average reward and num of actions
average_reward = np.mean(np.array(reward_list))
average_actions = np.mean(np.array(actions_count_list))
print("\nAverage reward: ", average_reward)
print("Average number of actions: ", average_actions)