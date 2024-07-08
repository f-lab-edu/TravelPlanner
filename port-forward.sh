#!/bin/bash

# Forward traffic from port 5045 to port 5044
socat TCP4-LISTEN:5045,fork TCP4:127.0.0.1:5044