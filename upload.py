import os
import argparse
import json
import requests
import base64
import shutil

CLIENT_ID = 'YmF0dGxlY29kZXdlYmFwcDpKQlVZOVZFNjkyNDNCWUM5MDI0Mzg3SEdWWTNBUUZL'


def get_token(username, password):
    headers = {'authorization': "Basic " + CLIENT_ID}
    data = {
        'grant_type': 'password',
        'username': username,
        'password': password,
        'client_id': CLIENT_ID,
    }
    req = requests.post("http://www.battlecode.org/oauth/token", headers=headers, data=data)
    print(req.text)
    return req


def upload_scrim_server(username, password, file_name, player_name):
    cwd = os.getcwd()
    if 'NODOCKER' in os.environ:
        os.chdir('..')
    else:
        os.chdir('/player')
    os.chdir(file_name)
    zip_file_name = os.path.abspath(os.path.join('../', file_name))

    shutil.make_archive(zip_file_name, 'zip', '.')
    if not zip_file_name.endswith('.zip'):
        zip_file_name += '.zip'

    os.chdir(cwd)
    req = get_token(username, password)
    if req.status_code != 200:
        print("Error authenticating.")
        return "Error authenticating."

    token = json.loads(req.text)['access_token']
    headers = {'Authorization': 'Bearer ' + token}
    data = {'label': player_name}
    with open(zip_file_name, 'rb') as image_file:
        encoded_string = base64.b64encode(image_file.read())
    data['src'] = encoded_string
    res = requests.post("https://battlecode.org/apis/submissions", headers=headers, data=data)
    return "success"


parser = argparse.ArgumentParser(
    "battlecode.sh",
    description='Run BattleCode 2018 matches'
)

parser.add_argument('-u', '--username', help="Username to battlecode.org", required=True)
parser.add_argument('-p', '--password', help="Password to battlecode.org", required=True)
parser.add_argument('-f', '--file', help="File name of bot", required=True)
parser.add_argument('-l', '--label', help="Name for bot", required=True)

args = parser.parse_args()
map_path = args.map


# Input validation
def validate_player_dir(path):
    if not os.path.exists(path):
        return "Cannot find the directory '" + path + "'. You should pass a relative or absolute path to a directory " \
                                                      "with your player code "

    if not os.path.isdir(path):
        return "'" + path + "' is not a directory. You should pass a relative or absolute path to a directory with " \
                            "your player code "

    if not os.path.exists(os.path.join(path, "run.sh")):
        return "Your player directory ('" + path + "') does not contain a run.sh file. See the example player folders " \
                                                   "to see how it should look. "

    return None


player_err = validate_player_dir(args.file)
if player_err is not None:
    print("Player: " + player_err)
    exit(1)

upload_scrim_server(args.username, args.password, args.file, args.label)
