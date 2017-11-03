import argparse
from InformationRetrieval import InformationRetrieval

parser = argparse.ArgumentParser()
parser.add_argument('-api_key',
                    type=str,
                    default='AIzaSyAHzQAbQFJmGyElhnh_VVFay_ECunRqVoE',
                    help='google api key')
parser.add_argument('-cse_id',
                    type=str,
                    default='009650898989487274447:ghd3zgarfa4',
                    help='google engine id')
parser.add_argument('-precision',
                    type=float,
                    default=0.9,
                    help='desired precision')
parser.add_argument('-query',
                    type=str,
                    default='jaguar',
                    help='query terms')
args = parser.parse_args()


if not (0<=args.precision<=1):
    print('invalid precision')
else:
    ir = InformationRetrieval(api_key=args.api_key,
                              cse_id=args.cse_id,
                              initial_query_terms=args.query.strip().split(),
                              wanted_precision=args.precision)
    ir.main_loops()


